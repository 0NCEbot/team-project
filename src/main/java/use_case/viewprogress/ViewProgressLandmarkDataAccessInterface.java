package use_case.viewprogress;

import entity.Landmark;

import java.util.List;

/**
 * Data Access Interface for retrieving landmark data in the View Progress Use Case.
 * Defines the contract for accessing landmark information from the data layer.
 * This interface follows the Interface Segregation Principle by exposing only
 * the methods needed for this specific use case.
 */
public interface ViewProgressLandmarkDataAccessInterface {

    /**
     * Retrieves all available landmarks.
     * Used to calculate the total number of landmarks for progress calculation.
     *
     * @return list of all landmarks
     */
    List<Landmark> getLandmarks();
}
