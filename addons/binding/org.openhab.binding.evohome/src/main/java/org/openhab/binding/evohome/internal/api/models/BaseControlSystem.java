package org.openhab.binding.evohome.internal.api.models;

public abstract class BaseControlSystem implements ControlSystem  {

    private String id;
    private String name;

    public BaseControlSystem(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

}
