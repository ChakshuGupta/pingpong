package edu.uci.iotproject.analysis;

import edu.uci.iotproject.io.PcapHandleReader;
import org.pcap4j.core.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class TriggerTrafficExtractor implements PcapPacketFilter {

    private final String mPcapFilePath;
    private final List<Instant> mTriggerTimes;
    private final String mDeviceIp;

    private int mTriggerIndex = 0;


    private static final int INCLUSION_WINDOW_MILLIS = 3_000;

    public TriggerTrafficExtractor(String pcapFilePath, List<Instant> triggerTimes, String deviceIp) throws PcapNativeException, NotOpenException {
        mPcapFilePath = pcapFilePath;
        // Ensure that trigger times are sorted in ascending as we rely on this fact in the logic that works out if a
        // packet is related to a trigger.
        Collections.sort(triggerTimes, (i1, i2) -> {
            if (i1.isBefore(i2)) return -1;
            else if (i2.isBefore(i1)) return 1;
            else return 0;
        });
        mTriggerTimes = Collections.unmodifiableList(triggerTimes);
        mDeviceIp = deviceIp;
    }


    public void performExtraction(PacketListener... extractedPacketsConsumers) throws PcapNativeException, NotOpenException, TimeoutException {
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(mPcapFilePath, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(mPcapFilePath);
        }
        // Use the native support for BPF to immediately filter irrelevant traffic.
        handle.setFilter("ip host " + mDeviceIp, BpfProgram.BpfCompileMode.OPTIMIZE);
        PcapHandleReader pcapReader = new PcapHandleReader(handle, this, extractedPacketsConsumers);
        pcapReader.readFromHandle();
        // Reset trigger index (in case client code chooses to rerun the extraction)
        mTriggerIndex = 0;
    }

    @Override
    public boolean shouldIncludePacket(PcapPacket packet) {
        if (mTriggerIndex >= mTriggerTimes.size()) {
            // Don't include packet if we've exhausted the list of trigger times.
            return false;
        }

        // TODO hmm, is this correct?
        Instant trigger = mTriggerTimes.get(mTriggerIndex);
        if (trigger.isBefore(packet.getTimestamp()) &&
                packet.getTimestamp().isBefore(trigger.plusMillis(INCLUSION_WINDOW_MILLIS))) {
            // Packet lies within INCLUSION_WINDOW_MILLIS after currently considered trigger, include it.
            return true;
        } else {
            if (!trigger.isBefore(packet.getTimestamp())) {
                // Packet is before currently considered trigger, so it shouldn't be included
                return false;
            } else {
                // Packet is >= INCLUSION_WINDOW_MILLIS after currently considered trigger.
                // Proceed to next trigger to see if it lies in range of that.
                // Note that there's an assumption here that no two trigger intervals don't overlap!
                mTriggerIndex++;
                return shouldIncludePacket(packet);
            }
        }
    }

}
