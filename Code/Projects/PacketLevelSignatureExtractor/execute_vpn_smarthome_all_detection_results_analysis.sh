#!/bin/bash

# Base directory where the smarthome evaluation traces and timestamp files are stored,
# (i.e., /some/arbitrary/local/path/experimental_result/smarthome)
TIMESTAMPS_BASE_DIR=$1
readonly TIMESTAMPS_BASE_DIR

# Base directory for the detection results files for the smarthome experiment
RESULTS_BASE_DIR=$2
readonly RESULTS_BASE_DIR

# ==================================================== AMAZON PLUG =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/amazon-plug/timestamps/amazon-plug-smarthome-apr-23-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/amazon-plug/amazon-plug.eth0.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ==================================================== ARLO CAMERA =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/arlo-camera/timestamps/arlo-camera-smarthome-nov-15-2018.timestamps"
RESULTS_FILE="$RESULTS_BASE_DIR/arlo-camera/arlo-camera.eth0.detection.pcap___phone-side.detectionresults"
# Put the analysis results in the same folder as the detection results.
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"


PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ============================================= BLOSSOM SPRINKLER QUICK RUN ============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/timestamps/blossom-sprinkler-quickrun-smarthome-jan-14-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/blossom-sprinkler-quickrun.eth0.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="false"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/blossom-sprinkler-quickrun.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="false"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ============================================== BLOSSOM SPRINKLER MODE ================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/timestamps/blossom-sprinkler-mode-smarthome-apr-22-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/blossom-sprinkler-mode.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ==================================================== D-LINK PLUG =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/dlink-plug/timestamps/dlink-plug-smarthome-nov-8-2018.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-plug/dlink-plug.eth0.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-plug/dlink-plug.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ==================================================== D-LINK SIREN ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/dlink-siren/timestamps/dlink-siren-smarthome-nov-10-2018.timestamps"

#PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-siren/dlink-siren.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== ECOBEE THERMOSTAT HVAC ===============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/timestamps/ecobee-thermostat-hvac-smarthome-apr-24-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/ecobee-thermostat-hvac.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== ECOBEE THERMOSTAT FAN ================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/timestamps/ecobee-thermostat-fan-smarthome-apr-24-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/ecobee-thermostat-fan.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ================================================= KWIKSET DOORLOCK ===================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/kwikset-doorlock/timestamps/kwikset-doorlock-smarthome-nov-10-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/kwikset-doorlock/kwikset-doorlock.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ================================================= NEST THERMOSTAT ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/nest-thermostat/timestamps/nest-thermostat-smarthome-nov-16-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/nest-thermostat/nest-thermostat.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ============================================== RACHIO SPRINKLER QUICK RUN ============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/timestamps/rachio-sprinkler-quickrun-smarthome-apr-25-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/rachio-sprinkler-quickrun.eth0.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ================================================= RACHIO SPRINKLER MODE ==============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/timestamps/rachio-sprinkler-mode-smarthome-apr-25-2019.timestamps"

# This one is going to generate 100 FPs because every event is counted twice (same signatures for ON and OFF).
# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/rachio-sprinkler-mode.eth0.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ===================================================== RING ALARM =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/ring-alarm/timestamps/ring-alarm-smarthome-apr-26-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/ring-alarm/ring-alarm.eth0.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ================================================= ROOMBA VACUUM ROBOT ================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/roomba-vacuum-robot/timestamps/roomba-vacuum-robot-smarthome-apr-27-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/roomba-vacuum-robot/roomba-vacuum-robot.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== SENGLED BULB ON/OFF ==================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/sengled-bulb/sengled-bulb-onoff/timestamps/sengled-bulb-onoff-smarthome-apr-23-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/sengled-bulb/sengled-bulb-onoff/sengled-bulb-onoff.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== SENGLED BULB INTENSITY ===============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/sengled-bulb/sengled-bulb-intensity/timestamps/sengled-bulb-intensity-smarthome-apr-24-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/sengled-bulb/sengled-bulb-intensity/sengled-bulb-intensity.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ====================================================== ST PLUG =======================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/st-plug/timestamps/st-plug-smarthome-nov-13-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/st-plug/st-plug.eth0.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ==================================================== TP-LINK PLUG ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/tplink-plug/timestamps/tplink-plug-smarthome-nov-9-2018.timestamps"

# DEVICE SIDE OUTBOUND
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-plug/tplink-plug.eth0.detection.pcap___device-side-outbound.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================