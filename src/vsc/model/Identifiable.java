package vsc.model;

import java.io.Serializable;

/** Anything stored in a repository must expose a unique id. */
// OOP: INTERFACE - a promise that a class has getId(). Lets one generic repository store anything.
public interface Identifiable extends Serializable {
    String getId();
}
