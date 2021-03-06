package com.fherfurt.HappyPlant.helper;

import com.fherfurt.HappyPlant.model.SensorDataEntry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/*
Class used to generate test data for plants which is formatted like real sensordata.
Uses the wateringBorder of the given plant and creates a data history from now until a given time period (default 7 Days = 168h)
 */
public class DataFaker {
    public static final int DEFAULT_BACKTRACK_LENGTH = 168;         // 7 Days in hours
    private static final int HOURS_BETWEEN_WATERING = 72;           // Used to determine waterConsumption related to wateringVolume
    private static final double STARTING_MOISTURE_RANGE_MIN = 10;   // starting % above wateringBorder
    private static final double STARTING_MOISTURE_RANGE = 15;       // Range above start

    public DataFaker() {
    }

    // Creates a new set of SensorData
    public ArrayList<SensorDataEntry> createSensorDataHistory(int backTrackLength, double wateringBorder) {
        ArrayList<SensorDataEntry> sensorData = new ArrayList<>();     // Resulting DataSets
        LocalDateTime timestamp = LocalDateTime.now();
        Random random = new Random();
        double moisture = (STARTING_MOISTURE_RANGE_MIN + wateringBorder) +
                STARTING_MOISTURE_RANGE * random.nextDouble();
        double wateringVolume = moisture - wateringBorder;                  // Each watering shall increase the moisture by this difference -> Resets the moisture to starting-value
        double waterConsumption = wateringVolume / ((HOURS_BETWEEN_WATERING * 0.25) +
                (HOURS_BETWEEN_WATERING * 2.0 - HOURS_BETWEEN_WATERING * 0.25) * random.nextDouble());  // Randomize the length of watering intervalls

        for (int i = 0; i < backTrackLength; i++)   // Each hour shall create a new Entry starting from the furthest day to "now"
        {
            sensorData.add(new SensorDataEntry(timestamp.minusHours(backTrackLength - i), moisture));

            if (moisture < wateringBorder)  // Needs watering
            {
                moisture += wateringVolume;
            } else                          // Consumes water between 0.5 or 1.5 times of the normal waterConsumption to represent different conditions (temperature, sunlight,...)
            {
                moisture -= (waterConsumption * 0.5) + (waterConsumption * 1.5 - waterConsumption * 0.5) * random.nextDouble();
            }
        }

        return sensorData;
    }

    /*
    Inconsistency of the Graph would get lost over the course of multiple updates. The starting ranges would need to be saved per plant.
    This endeavor is too intense for our testing purposes. We rather create a new graph every time even though it lacks performance.

    // Updates the given set of SensorData to be up to date (now) with a given length of history
    public Vector<SensorData> updateSensorDataHistory(int backTrackLength, double wateringBorder, Vector<SensorData> sensorDataVector)
    {
        if(sensorDataVector.lastElement().getTimestamp().isBefore(LocalDateTime.now().minusHours(backTrackLength)) )    // Old History is older than backTrackLength
        {
            sensorDataVector = this.createSensorDataHistory(backTrackLength, wateringBorder);
        }
        else
        {
            // Where can we calculate the ranges which where used before? More complexity than simply creating a new graph instead?
            // double moisture = sensorDataVector.firstElement().getMoisture();     // This will destroy the consistency of the graph when done more than once...
        }
        return sensorDataVector;
    }
    */
}
