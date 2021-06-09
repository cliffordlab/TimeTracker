package org.hdm.app.timetracker.models;

import android.media.Image;

/**
 * Data representation of a Subject.
 */
public class SubjectModel {

    /**
     * Attributes
     */
    private final Image picture;
    private final String name;
    private final int age;
    private final String gender;
    private final String education;

    private final String tribe;
    private final int household;
    private final int size;
    private final int weight;

    private final int landOwned;
    private final int landCultivated;

    /**
     * Constructor
     */
    public SubjectModel(
            Image _picture,
            String _name,
            int _age,
            String _gender,
            String _education,
            String _tribe,
            int _household,
            int _size,
            int _weight,
            int _landOwned,
            int _landCultivated
    ) {
        this.picture = _picture;
        this.name = _name;
        this.age = _age;
        this.gender = _gender;
        this.education = _education;

        this.tribe = _tribe;
        this.household = _household;
        this.size = _size;
        this.weight = _weight;

        this.landOwned = _landOwned;
        this.landCultivated = _landCultivated;
    }

}
