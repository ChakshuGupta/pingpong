package edu.uci.iotproject;

import org.pcap4j.core.*;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;
/**
 * Opens and reads from a pcap file.
 * This class is nothing but a simple wrapper around some functionality in {@link Pcaps} and {@link PcapHandle} which
 * serves to simplify client code.
 * Note that the file is read in offline mode, i.e., this class does not support live processing of packets.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class PcapReader {

    private final PcapHandle mHandle;

    /**
     * Create a new {@code PcapReader} that reads the file specified by the absolute path {@code fileName}.
     * @param fileName The absolute path to the pcap file to be read.
     * @param berkeleyPacketFilter A Berkeley Packet Filter to be applied when reading the PCAP file to filter out
     *                             unwanted packets immediately. May be {@code null} if no filter is to be applied.
     * @throws PcapNativeException If an error occurs in the pcap native library.
     * @throws NotOpenException If the pcap file cannot be opened.
     */
    public PcapReader(String fileName, String berkeleyPacketFilter) throws PcapNativeException, NotOpenException {
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(fileName, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(fileName);
        }
        if(!handle.isOpen()) {
            throw new NotOpenException("could not open pcap file " + fileName);
        }
        if (berkeleyPacketFilter != null) {
            handle.setFilter(berkeleyPacketFilter, BpfProgram.BpfCompileMode.OPTIMIZE);
        }
        mHandle = handle;
    }

    /**
     * Reads the next packet in the pcap file.
     * @return The next packet in the pcap file, or {@code null} if all packets have been read.
     */
    public PcapPacket readNextPacket() {
        try {
            return mHandle.getNextPacketEx();
        } catch (EOFException eofe) {
            return null;
        } catch (PcapNativeException|TimeoutException|NotOpenException e) {
            // Wrap checked exceptions in unchecked exceptions to simplify client code.
            throw new RuntimeException(e);
        }
    }

}
