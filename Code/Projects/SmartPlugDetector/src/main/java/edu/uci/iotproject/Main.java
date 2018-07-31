package edu.uci.iotproject;

import edu.uci.iotproject.analysis.TcpConversationUtils;
import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
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
        // D-Link July 26 experiment
        final String inputPcapFile = "/Users/varmarken/temp/UCI IoT Project/experiments/2018-07/dlink/dlink.wlan1.local.pcap";
        final String outputPcapFile = "/Users/varmarken/temp/UCI IoT Project/experiments/2018-07/dlink/dlink-processed.pcap";
        final String triggerTimesFile = "/Users/varmarken/temp/UCI IoT Project/experiments/2018-07/dlink/dlink-july-26-2018.timestamps";
        final String deviceIp = "192.168.1.246";
        // TP-Link July 25 experiment
//        final String inputPcapFile = "/Users/varmarken/temp/UCI IoT Project/experiments/2018-07/tplink/tplink.wlan1.local.pcap";
//        final String outputPcapFile = "/Users/varmarken/temp/UCI IoT Project/experiments/2018-07/tplink/tplink-processed.pcap";
//        final String triggerTimesFile = "/Users/varmarken/temp/UCI IoT Project/experiments/2018-07/tplink/tplink-july-25-2018.timestamps";
//        final String deviceIp = "192.168.1.159";

        TriggerTimesFileReader ttfr = new TriggerTimesFileReader();
        List<Instant> triggerTimes = ttfr.readTriggerTimes(triggerTimesFile, false);
        TriggerTrafficExtractor tte = new TriggerTrafficExtractor(inputPcapFile, triggerTimes, deviceIp);
        final PcapDumper outputter = Pcaps.openDead(DataLinkType.EN10MB, 65536).dumpOpen(outputPcapFile);
        DnsMap dnsMap = new DnsMap();
        TcpReassembler tcpReassembler = new TcpReassembler();
        tte.performExtraction(pkt -> {
            try {
                outputter.dump(pkt);
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }, dnsMap, tcpReassembler);
        outputter.flush();
        outputter.close();

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
        // -------------------------------------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------------------------------------
    }

}


// TP-Link MAC 50:c7:bf:33:1f:09 and usually IP 192.168.1.159 (remember to verify per file)