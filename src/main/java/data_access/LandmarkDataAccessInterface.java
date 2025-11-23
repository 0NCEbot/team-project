// src/java/data_access/LandmarkDataAccessInterface.java
package data_access;

import entity.Landmark;
import use_case.viewprogress.ViewProgressLandmarkDataAccessInterface;

import java.util.List;

/**
 * Data Access Interface for landmark operations.
 * Extends ViewProgressLandmarkDataAccessInterface to support the View Progress use case.
 * This follows the Interface Segregation Principle by extending specific use case interfaces.
 */
public interface LandmarkDataAccessInterface extends ViewProgressLandmarkDataAccessInterface {

    /**
     * Get a list of landmarks.
     * @return a list of Landmark objects
     */
    List<Landmark> getLandmarks();

    /**
     * Check if the landmark exists.
     * @param landmarkName the landmark name
     * @return True if the landmark associated with the name exists, false otherwise
     */
    boolean existsByName(String landmarkName);
    Landmark findByName(String name);
}
