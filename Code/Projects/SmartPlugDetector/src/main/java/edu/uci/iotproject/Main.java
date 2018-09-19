package edu.uci.iotproject;

import static edu.uci.iotproject.analysis.UserAction.Type;

import edu.uci.iotproject.analysis.*;
import edu.uci.iotproject.comparison.seqalignment.ExtractedSequence;
import edu.uci.iotproject.comparison.seqalignment.SequenceAlignment;
import edu.uci.iotproject.comparison.seqalignment.SequenceExtraction;
import edu.uci.iotproject.io.TriggerTimesFileReader;
import edu.uci.iotproject.util.PcapPacketUtils;
import edu.uci.iotproject.util.PrintUtils;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.DBSCANClusterer;
import org.pcap4j.core.*;
import org.pcap4j.packet.namednumber.DataLinkType;

import java.io.EOFException;
import java.io.File;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
//        String path = "/Users/varmarken/temp/UCI IoT Project/experiments"; // Janus
        boolean verbose = true;
        final String onPairsPath = "/scratch/July-2018/on.txt";
        final String offPairsPath = "/scratch/July-2018/off.txt";

        // 1) D-Link July 26 experiment
        final String inputPcapFile = path + "/2018-07/dlink/dlink.wlan1.local.pcap";
        final String outputPcapFile = path + "/2018-07/dlink/dlink-processed.pcap";
        final String triggerTimesFile = path + "/2018-07/dlink/dlink-july-26-2018.timestamps";
        final String deviceIp = "192.168.1.246"; // .246 == phone; .199 == dlink plug?

        // 2) TP-Link July 25 experiment
//        final String inputPcapFile = path + "/2018-07/tplink/tplink.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-07/tplink/tplink-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/tplink/tplink-july-25-2018.timestamps";
//        final String deviceIp = "192.168.1.159";

        // 2b) TP-Link July 25 experiment TRUNCATED:
        // Only contains "true local" events, i.e., before the behavior changes to remote-like behavior.
        // Last included event is at July 25 10:38:11; file filtered to only include packets with arrival time <= 10:38:27.
//        final String inputPcapFile = path + "/2018-07/tplink/tplink.wlan1.local.truncated.pcap";
//        final String outputPcapFile = path + "/2018-07/tplink/tplink-processed.truncated.pcap";
//        final String triggerTimesFile = path + "/2018-07/tplink/tplink-july-25-2018.truncated.timestamps";
//        final String deviceIp = "192.168.1.159";

        // 3) SmartThings Plug July 25 experiment
//        final String inputPcapFile = path + "/2018-07/stplug/stplug.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-07/stplug/stplug-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/stplug/smartthings-july-25-2018.timestamps";
//        final String deviceIp = "192.168.1.246"; // .246 == phone; .142 == SmartThings Hub (note: use eth0 capture for this!)

        // 4) Wemo July 30 experiment
//        final String inputPcapFile = path + "/2018-07/wemo/wemo.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-07/wemo/wemo-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/wemo/wemo-july-30-2018.timestamps";
//        final String deviceIp = "192.168.1.145";

        // 5) Wemo Insight July 31 experiment
//        final String inputPcapFile = path + "/2018-07/wemoinsight/wemoinsight.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-07/wemoinsight/wemoinsight-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/wemoinsight/wemo-insight-july-31-2018.timestamps";
//        final String deviceIp = "192.168.1.135";

        // 6) TP-Link Bulb August 1 experiment
//        final String inputPcapFile = path + "/2018-08/tplink-bulb/tplinkbulb.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/tplink-bulb/tplinkbulb-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/tplink-bulb/tplink-bulb-aug-3-2018.timestamps";
//        final String deviceIp = "192.168.1.140"; // .246 == phone; .140 == TP-Link bulb

        // 7) Kwikset Doorlock August 6 experiment
//        final String inputPcapFile = path + "/2018-08/kwikset-doorlock/kwikset-doorlock.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/kwikset-doorlock/kwikset-doorlock-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/kwikset-doorlock/kwikset-doorlock-aug-6-2018.timestamps";
//        final String deviceIp = "192.168.1.246"; // .246 == phone; .142 == SmartThings Hub (note: use eth0 capture for this!)

        // September 12, 2018 - includes both wlan1 and eth1 interfaces
        //final String inputPcapFile = path + "/2018-08/kwikset-doorlock/kwikset3.wlan1.local.pcap";
//        final String inputPcapFile = path + "/2018-08/kwikset-doorlock/kwikset3.eth1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/kwikset-doorlock/kwikset3-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/kwikset-doorlock/kwikset-doorlock-sept-12-2018.timestamps";
//        final String deviceIp = "192.168.1.142"; // .246 == phone; .142 == SmartThings Hub (note: use eth0 capture for this!)

        // 8) Hue Bulb August 7 experiment
//        final String inputPcapFile = path + "/2018-08/hue-bulb/hue-bulb.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/hue-bulb/hue-bulb-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/hue-bulb/hue-bulb-aug-7-2018.timestamps";
//        final String deviceIp = "192.168.1.246";

        // 9) Lifx Bulb August 8 experiment
//        final String inputPcapFile = path + "/2018-08/lifx-bulb/lifx-bulb.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/lifx-bulb/lifx-bulb-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/lifx-bulb/lifx-bulb-aug-8-2018.timestamps";
//        final String deviceIp = "192.168.1.246"; // .246 == phone; .231 == Lifx

        // 10) Amcrest Camera August 9 experiment
//        final String inputPcapFile = path + "/2018-08/amcrest-camera/amcrest-camera.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/amcrest-camera/amcrest-camera-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/amcrest-camera/amcrest-camera-aug-9-2018.timestamps";
//        final String deviceIp = "192.168.1.246"; // .246 == phone; .235 == camera

        // 11) Arlo Camera August 10 experiment
//        final String inputPcapFile = path + "/2018-08/arlo-camera/arlo-camera.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/arlo-camera/arlo-camera-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/arlo-camera/arlo-camera-aug-10-2018.timestamps";
//        final String deviceIp = "192.168.1.140"; // .246 == phone; .140 == camera

        // 12) Blossom sprinkler August 13 experiment
//        final String inputPcapFile = path + "/2018-08/blossom/blossom.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/blossom/blossom-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/blossom/blossom-aug-13-2018.timestamps";
//        final String deviceIp = "192.168.1.246"; // .246 == phone; .229 == sprinkler

//        // 13) DLink siren August 14 experiment
//        final String inputPcapFile = path + "/2018-08/dlink-siren/dlink-siren.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/dlink-siren/dlink-siren-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/dlink-siren/dlink-siren-aug-14-2018.timestamps";
//        final String deviceIp = "192.168.1.246"; // .246 == phone; .183 == siren

        // 14) Nest thermostat August 15 experiment
//        final String inputPcapFile = path + "/2018-08/nest/nest.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/nest/nest-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/nest/nest-aug-15-2018.timestamps";
//        final String deviceIp = "192.168.1.246"; // .246 == phone; .127 == Nest thermostat

        // 15) Alexa August 16 experiment
//        final String inputPcapFile = path + "/2018-08/alexa/alexa.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/alexa/alexa-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/alexa/alexa-aug-16-2018.timestamps";
//        final String deviceIp = "192.168.1.225"; // .246 == phone; .225 == Alexa
        // August 17
//        final String inputPcapFile = path + "/2018-08/alexa/alexa2.wlan1.local.pcap";
//        final String outputPcapFile = path + "/2018-08/alexa/alexa2-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/alexa/alexa-aug-17-2018.timestamps";
//        final String deviceIp = "192.168.1.225"; // .246 == phone; .225 == Alexa

        // September 17
//        final String inputPcapFile = path + "/2018-08/noise/noise.eth1.pcap";
//        final String outputPcapFile = path + "/2018-08/noise/noise-processed.pcap";
//        final String triggerTimesFile = path + "/2018-08/noise/noise-sept-17-2018.timestamps";
//        final String deviceIp = "192.168.1.142"; //  .142 == SmartThings Hub; .199 == dlink plug; .183 == siren

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



        /*
         * NOTE: no need to generate these more complex on/off maps that also contain mappings from hostname and
         * sequence identifiers as we do not care about hostnames and sequences during clustering.
         * We can simply use the UserAction->List<Conversation> map to generate ON/OFF groupings of conversations.
         */
        /*
        // Contains all ON events: hostname -> sequence identifier -> list of conversations with that sequence
        Map<String, Map<String, List<Conversation>>> ons = new HashMap<>();
        // Contains all OFF events: hostname -> sequence identifier -> list of conversations with that sequence
        Map<String, Map<String, List<Conversation>>> offs = new HashMap<>();
        userActionsToConvsByHostname.forEach((ua, hostnameToConvs) -> {
            Map<String, Map<String, List<Conversation>>> outer = ua.getType() == Type.TOGGLE_ON ? ons : offs;
            hostnameToConvs.forEach((host, convs) -> {
                Map<String, List<Conversation>> seqsToConvs = TcpConversationUtils.
                        groupConversationsByPacketSequence(convs, verbose);
                outer.merge(host, seqsToConvs, (oldMap, newMap) -> {
                    newMap.forEach((sequence, cs) -> oldMap.merge(sequence, cs, (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    }));
                    return oldMap;
                });
            });
        });
        */

        // ================================================ CLUSTERING ================================================
        // Note: no need to use the more convoluted on/off maps; can simply use the UserAction->List<Conversation> map
        // when don't care about hostnames and sequences (see comment earlier).
        List<Conversation> onConversations = userActionToConversations.entrySet().stream().
                filter(e -> e.getKey().getType() == Type.TOGGLE_ON). // drop all OFF events from stream
                map(e -> e.getValue()). // no longer interested in the UserActions
                flatMap(List::stream). // flatten List<List<T>> to a List<T>
                collect(Collectors.toList());
        List<Conversation> offConversations = userActionToConversations.entrySet().stream().
                filter(e -> e.getKey().getType() == Type.TOGGLE_OFF).
                map(e -> e.getValue()).
                flatMap(List::stream).
                collect(Collectors.toList());
        List<PcapPacketPair> onPairs = onConversations.stream().
                map(c -> c.isTls() ? TcpConversationUtils.extractTlsAppDataPacketPairs(c) :
                        TcpConversationUtils.extractPacketPairs(c)).
                flatMap(List::stream). // flatten List<List<>> to List<>
                collect(Collectors.toList());
        List<PcapPacketPair> offPairs = offConversations.stream().
                map(c -> c.isTls() ? TcpConversationUtils.extractTlsAppDataPacketPairs(c) :
                        TcpConversationUtils.extractPacketPairs(c)).
                flatMap(List::stream). // flatten List<List<>> to List<>
                collect(Collectors.toList());
        // Note: need to update the DnsMap of all PcapPacketPairs if we want to use the IP/hostname-sensitive distance.
        Stream.concat(Stream.of(onPairs), Stream.of(offPairs)).flatMap(List::stream).forEach(p -> p.setDnsMap(dnsMap));
        // Perform clustering on conversation logged as part of all ON events.
        DBSCANClusterer<PcapPacketPair> onClusterer = new DBSCANClusterer<>(10.0, 45);
        List<Cluster<PcapPacketPair>> onClusters = onClusterer.cluster(onPairs);
        // Perform clustering on conversation logged as part of all OFF events.
        DBSCANClusterer<PcapPacketPair> offClusterer = new DBSCANClusterer<>(10.0, 45);
        List<Cluster<PcapPacketPair>> offClusters = offClusterer.cluster(offPairs);
        // Output clusters
        System.out.println("========================================");
        System.out.println("       Clustering results for ON        ");
        System.out.println("       Number of clusters: " + onClusters.size());
        int count = 0;
        List<List<PcapPacket>> ppListOfListReadOn = null;
        for (Cluster<PcapPacketPair> c : onClusters) {
            System.out.println(String.format("<<< Cluster #%02d (%03d points) >>>", ++count, c.getPoints().size()));
            System.out.print(PrintUtils.toSummaryString(c));
            // Print to file
            List<List<PcapPacket>> ppListOfList = PcapPacketUtils.clusterToListOfPcapPackets(c);
            PrintUtils.serializeClustersIntoFile("./onSignature" + count + ".sig", ppListOfList);
            ppListOfListReadOn =
                    PrintUtils.serializeClustersFromFile("./onSignature" + count + ".sig");
        }
        System.out.println("========================================");
        System.out.println("       Clustering results for OFF       ");
        System.out.println("       Number of clusters: " + offClusters.size());
        count = 0;
        List<List<PcapPacket>> ppListOfListReadOff = null;
        for (Cluster<PcapPacketPair> c : offClusters) {
            System.out.println(String.format("<<< Cluster #%03d (%06d points) >>>", ++count, c.getPoints().size()));
            System.out.print(PrintUtils.toSummaryString(c));
            // Print to file
            List<List<PcapPacket>> ppListOfList = PcapPacketUtils.clusterToListOfPcapPackets(c);
            PrintUtils.serializeClustersIntoFile("./offSignature" + count + ".sig", ppListOfList);
            ppListOfListReadOff =
                    PrintUtils.serializeClustersFromFile("./offSignature" + count + ".sig");
        }
        System.out.println("========================================");
        // ============================================================================================================

        /*
        System.out.println("==== ON ====");
        // Print out all the pairs into a file for ON events
        File fileOnEvents = new File(onPairsPath);
        PrintWriter pwOn = null;
        try {
            pwOn = new PrintWriter(fileOnEvents);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        for(Map.Entry<String, Map<String, List<Conversation>>> entry : ons.entrySet()) {
            Map<String, List<Conversation>> seqsToConvs = entry.getValue();
            for(Map.Entry<String, List<Conversation>> entryConv : seqsToConvs.entrySet()) {
                List<Conversation> listConv = entryConv.getValue();
                // Just get the first Conversation because all Conversations in this group
                // should have the same pairs of Application Data.
                for(Conversation conv : listConv) {
                    // Process only if it is a TLS packet
                    if (conv.isTls()) {
                        List<PcapPacketPair> tlsAppDataList = TcpConversationUtils.extractTlsAppDataPacketPairs(conv);
                        for(PcapPacketPair pair: tlsAppDataList) {
                            System.out.println(PrintUtils.toCsv(pair, dnsMap));
                            pwOn.println(PrintUtils.toCsv(pair, dnsMap));
                        }
                    } else { // Non-TLS conversations
                        List<PcapPacketPair> packetList = TcpConversationUtils.extractPacketPairs(conv);
                        for(PcapPacketPair pair: packetList) {
                            System.out.println(PrintUtils.toCsv(pair, dnsMap));
                            pwOn.println(PrintUtils.toCsv(pair, dnsMap));
                        }
                    }
                }
            }
        }
        pwOn.close();

        System.out.println("==== OFF ====");
        // Print out all the pairs into a file for ON events
        File fileOffEvents = new File(offPairsPath);
        PrintWriter pwOff = null;
        try {
            pwOff = new PrintWriter(fileOffEvents);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        for(Map.Entry<String, Map<String, List<Conversation>>> entry : offs.entrySet()) {
            Map<String, List<Conversation>> seqsToConvs = entry.getValue();
            for(Map.Entry<String, List<Conversation>> entryConv : seqsToConvs.entrySet()) {
                List<Conversation> listConv = entryConv.getValue();
                // Just get the first Conversation because all Conversations in this group
                // should have the same pairs of Application Data.
                for(Conversation conv : listConv) {
                    // Process only if it is a TLS packet
                    if (conv.isTls()) {
                        List<PcapPacketPair> tlsAppDataList = TcpConversationUtils.extractTlsAppDataPacketPairs(conv);
                        for(PcapPacketPair pair: tlsAppDataList) {
                            System.out.println(PrintUtils.toCsv(pair, dnsMap));
                            pwOff.println(PrintUtils.toCsv(pair, dnsMap));
                        }
                    } else { // Non-TLS conversations
                        List<PcapPacketPair> packetList = TcpConversationUtils.extractPacketPairs(conv);
                        for (PcapPacketPair pair : packetList) {
                            System.out.println(PrintUtils.toCsv(pair, dnsMap));
                            pwOff.println(PrintUtils.toCsv(pair, dnsMap));
                        }
                    }
                }
            }
        }
        pwOff.close();
        */

//        // ================================================================================================
//        // <<< Some work-in-progress/explorative code that extracts a "representative" sequence >>>
//        //
//        // Currently need to know relevant hostname in advance :(
//        String hostname = "events.tplinkra.com";
////        String hostname = "rfe-us-west-1.dch.dlink.com";
//        // Conversations with 'hostname' for ON events.
//        List<Conversation> onsForHostname = new ArrayList<>();
//        // Conversations with 'hostname' for OFF events.
//        List<Conversation> offsForHostname = new ArrayList<>();
//        // "Unwrap" sequence groupings in ons/offs maps.
//        ons.get(hostname).forEach((k,v) -> onsForHostname.addAll(v));
//        offs.get(hostname).forEach((k,v) -> offsForHostname.addAll(v));
//
//
//        Map<String, List<Conversation>> onsForHostnameGroupedByTlsAppDataSequence = TcpConversationUtils.groupConversationsByTlsApplicationDataPacketSequence(onsForHostname);
//
//
//        // Extract representative sequence for ON and OFF by providing the list of conversations with
//        // 'hostname' observed for each event type (the training data).
//        SequenceExtraction seqExtraction = new SequenceExtraction();
////        ExtractedSequence extractedSequenceForOn = seqExtraction.extract(onsForHostname);
////        ExtractedSequence extractedSequenceForOff = seqExtraction.extract(offsForHostname);
//
//        ExtractedSequence extractedSequenceForOn = seqExtraction.extractByTlsAppData(onsForHostname);
//        ExtractedSequence extractedSequenceForOff = seqExtraction.extractByTlsAppData(offsForHostname);
//
//        // Let's check how many ONs align with OFFs and vice versa (that is, how many times an event is incorrectly
//        // labeled).
//        int onsLabeledAsOff = 0;
//        Integer[] representativeOnSeq = TcpConversationUtils.getPacketLengthSequence(extractedSequenceForOn.getRepresentativeSequence());
//        Integer[] representativeOffSeq = TcpConversationUtils.getPacketLengthSequence(extractedSequenceForOff.getRepresentativeSequence());
//        SequenceAlignment<Integer> seqAlg = seqExtraction.getAlignmentAlgorithm();
//        for (Conversation c : onsForHostname) {
//            Integer[] onSeq = TcpConversationUtils.getPacketLengthSequence(c);
//            if (seqAlg.calculateAlignment(representativeOffSeq, onSeq) <= extractedSequenceForOff.getMaxAlignmentCost()) {
//                onsLabeledAsOff++;
//            }
//        }
//        int offsLabeledAsOn = 0;
//        for (Conversation c : offsForHostname) {
//            Integer[] offSeq = TcpConversationUtils.getPacketLengthSequence(c);
//            if (seqAlg.calculateAlignment(representativeOnSeq, offSeq) <= extractedSequenceForOn.getMaxAlignmentCost()) {
//                offsLabeledAsOn++;
//            }
//        }
//        System.out.println("");
//        // ================================================================================================
//
//
//        // -------------------------------------------------------------------------------------------------------------
//        // -------------------------------------------------------------------------------------------------------------
    }

}


// TP-Link MAC 50:c7:bf:33:1f:09 and usually IP 192.168.1.159 (remember to verify per file)
// frame.len >= 556 && frame.len <= 558 && ip.addr == 192.168.1.159