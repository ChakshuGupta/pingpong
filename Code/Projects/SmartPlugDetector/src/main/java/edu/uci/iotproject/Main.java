package edu.uci.iotproject;

import static edu.uci.iotproject.analysis.UserAction.Type;

import edu.uci.iotproject.analysis.TcpConversationUtils;
import edu.uci.iotproject.analysis.TrafficLabeler;
import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.analysis.UserAction;
import edu.uci.iotproject.io.TriggerTimesFileReader;
import org.pcap4j.core.*;
import org.pcap4j.packet.namednumber.DataLinkType;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * This is a system that reads PCAP files to compare
 * patterns of DNS hostnames, packet sequences, and packet
 * lengths with training data to determine certain events
 * or actions for smart home devices.
 *
 * @author Janus Varmarken
 * @author Rahmadi Trimananda (rtrimana@uci.edu)
 * @version 0.1
 */
public class Main {


    public static void main(String[] args) throws PcapNativeException, NotOpenException, EOFException, TimeoutException, UnknownHostException {
        // -------------------------------------------------------------------------------------------------------------
        // ------------ # Code for extracting traffic generated by a device within x seconds of a trigger # ------------
        // Paths to input and output files (consider supplying these as arguments instead) and IP of the device for
        // which traffic is to be extracted:
        String path = "/scratch/July-2018"; // Rahmadi
        //String path = "/Users/varmarken/temp/UCI IoT Project/experiments"; // Janus

        // D-Link July 26 experiment
        final String inputPcapFile = path + "/2018-07/dlink/dlink.wlan1.local.pcap";
        final String outputPcapFile = path + "/2018-07/dlink/dlink-processed.pcap";
        final String triggerTimesFile = path + "/2018-07/dlink/dlink-july-26-2018.timestamps";
        final String deviceIp = "192.168.1.246"; // .246 == phone; .199 == dlink plug?

        // TP-Link July 25 experiment
//        final String inputPcapFile = path + "/2018-07/tplink/tplink.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-07/tplink/tplink-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/tplink/tplink-july-25-2018.timestamps";
//        final String deviceIp = "192.168.1.159";

        // SmartThings Plug July 25 experiment
//        final String inputPcapFile = path + "/2018-07/stplug/stplug.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-07/stplug/stplug-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/stplug/smartthings-july-25-2018.timestamps";
//        final String deviceIp = "192.168.1.246"; // .246 == phone; .142 == SmartThings Hub (note: use eth0 capture for this!)

        // Wemo July 30 experiment
//        final String inputPcapFile = path + "/2018-07/wemo/wemo.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-07/wemo/wemo-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/wemo/wemo-july-30-2018.timestamps";
//        final String deviceIp = "192.168.1.145";

        // Wemo Insight July 31 experiment
//        final String inputPcapFile = path + "/2018-07/wemoinsight/wemoinsight.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-07/wemoinsight/wemoinsight-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/wemoinsight/wemo-insight-july-31-2018.timestamps";
//        final String deviceIp = "192.168.1.135";

        // TP-Link BULB August 1 experiment
//        final String inputPcapFile = path + "/2018-08/tplink-bulb/tplinkbulb.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/tplink-bulb/tplinkbulb-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/tplink-bulb/tplink-bulb-aug-3-2018.timestamps";
//        final String deviceIp = "192.168.1.140";

        TriggerTimesFileReader ttfr = new TriggerTimesFileReader();
        List<Instant> triggerTimes = ttfr.readTriggerTimes(triggerTimesFile, false);
        // Tag each trigger with "ON" or "OFF", assuming that the first trigger is an "ON" and that they alternate.
        List<UserAction> userActions = new ArrayList<>();
        for (int i = 0; i < triggerTimes.size(); i++) {
            userActions.add(new UserAction(i % 2 == 0 ? Type.TOGGLE_ON : Type.TOGGLE_OFF, triggerTimes.get(i)));
        }
        TriggerTrafficExtractor tte = new TriggerTrafficExtractor(inputPcapFile, triggerTimes, deviceIp);
        final PcapDumper outputter = Pcaps.openDead(DataLinkType.EN10MB, 65536).dumpOpen(outputPcapFile);
        DnsMap dnsMap = new DnsMap();
        TcpReassembler tcpReassembler = new TcpReassembler();
        TrafficLabeler trafficLabeler = new TrafficLabeler(userActions);
        tte.performExtraction(pkt -> {
            try {
                outputter.dump(pkt);
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }, dnsMap, tcpReassembler, trafficLabeler);
        outputter.flush();
        outputter.close();

        if (tte.getPacketsIncludedCount() != trafficLabeler.getTotalPacketCount()) {
            // Sanity/debug check
            throw new AssertionError(String.format("mismatch between packet count in %s and %s",
                    TriggerTrafficExtractor.class.getSimpleName(), TrafficLabeler.class.getSimpleName()));
        }

        // Extract all conversations present in the filtered trace.
        List<Conversation> allConversations = tcpReassembler.getTcpConversations();
        // Group conversations by hostname.
        Map<String, List<Conversation>> convsByHostname = TcpConversationUtils.groupConversationsByHostname(allConversations, dnsMap);
        System.out.println("Grouped conversations by hostname.");
        // For each hostname, count the frequencies of packet lengths exchanged with that hostname.
        final Map<String, Map<Integer, Integer>> pktLenFreqsByHostname = new HashMap<>();
        convsByHostname.forEach((host, convs) -> pktLenFreqsByHostname.put(host, TcpConversationUtils.countPacketLengthFrequencies(convs)));
        System.out.println("Counted frequencies of packet lengths exchanged with each hostname.");
        // For each hostname, count the frequencies of packet sequences (i.e., count how many conversations exchange a
        // sequence of packets of some specific lengths).
        final Map<String, Map<String, Integer>> pktSeqFreqsByHostname = new HashMap<>();
        convsByHostname.forEach((host, convs) -> pktSeqFreqsByHostname.put(host, TcpConversationUtils.countPacketSequenceFrequencies(convs)));
        System.out.println("Counted frequencies of packet sequences exchanged with each hostname.");
        // For each hostname, count frequencies of packet pairs exchanged with that hostname across all conversations
        final Map<String, Map<String, Integer>> pktPairFreqsByHostname =
                TcpConversationUtils.countPacketPairFrequenciesByHostname(allConversations, dnsMap);
        System.out.println("Counted frequencies of packet pairs per hostname");
        // For each user action, reassemble the set of TCP connections occurring shortly after
        final Map<UserAction, List<Conversation>> userActionToConversations = trafficLabeler.getLabeledReassembledTcpTraffic();
        final Map<UserAction, Map<String, List<Conversation>>> userActionsToConvsByHostname = trafficLabeler.getLabeledReassembledTcpTraffic(dnsMap);
        System.out.println("Reassembled TCP conversations occurring shortly after each user event");



        // Contains all ON events: hostname -> sequence identifier -> list of conversations with that sequence
        Map<String, Map<String, List<Conversation>>> ons = new HashMap<>();
        // Contains all OFF events: hostname -> sequence identifier -> list of conversations with that sequence
        Map<String, Map<String, List<Conversation>>> offs = new HashMap<>();
        userActionsToConvsByHostname.forEach((ua, hostnameToConvs) -> {
            Map<String, Map<String, List<Conversation>>> outer = ua.getType() == Type.TOGGLE_ON ? ons : offs;
            hostnameToConvs.forEach((host, convs) -> {
                Map<String, List<Conversation>> seqsToConvs = TcpConversationUtils.
                        groupConversationsByPacketSequence(convs);
                outer.merge(host, seqsToConvs, (oldMap, newMap) -> {
                    newMap.forEach((sequence, cs) -> oldMap.merge(sequence, cs, (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    }));
                    return oldMap;
                });
            });
        });

        System.out.println("");

        // -------------------------------------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------------------------------------
    }

}


// TP-Link MAC 50:c7:bf:33:1f:09 and usually IP 192.168.1.159 (remember to verify per file)
// frame.len >= 556 && frame.len <= 558 && ip.addr == 192.168.1.159