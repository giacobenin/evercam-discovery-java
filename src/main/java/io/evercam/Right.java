package io.evercam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Right {
    private String rightsString;
    private final String SNAPSHOT = "snapshot";
    private final String VIEW = "view";
    private final String EDIT = "edit";
    private final String DELETE = "delete";
    private final String LIST = "list";
    private final String GRANT_SNAPSHOT = "grant~snapshot";
    private final String GRANT_VIEW = "grant~view";
    private final String GRANT_EDIT = "grant~edit";
    private final String GRANT_DELETE = "grant~delete";
    private final String GRANT_LIST = "grant~list";

    public final static String FULL_RIGHTS = "Snapshot,View,Edit,List";
    public final static String READ_ONLY = "Snapshot,List";

    public Right(String rightsString) {
        this.rightsString = rightsString.toLowerCase(Locale.UK);
    }

    private Right() {
        //Private constructor
    }

    public ArrayList<String> toArray() {
        String[] rightsArray = rightsString.split(",");
        return new ArrayList<String>(Arrays.asList(rightsArray));
    }

    public boolean canTakeSnapshot() {
        return toArray().contains(SNAPSHOT);
    }

    public boolean canEdit() {
        return toArray().contains(EDIT);
    }

    public boolean canView() {
        return toArray().contains(VIEW);
    }

    public boolean canDelete() {
        return toArray().contains(DELETE);
    }

    public boolean canList() {
        return toArray().contains(LIST);
    }

    public boolean canGrantSnapshot() {
        return toArray().contains(GRANT_SNAPSHOT);
    }

    public boolean canGrantEdit() {
        return toArray().contains(GRANT_EDIT);
    }

    public boolean canGrantView() {
        return toArray().contains(GRANT_VIEW);
    }

    public boolean canGrantDelete() {
        return toArray().contains(GRANT_DELETE);
    }

    public boolean canGrantList() {
        return toArray().contains(GRANT_LIST);
    }

    /**
     * Validate if the user has full right on this camera.
     *
     * @return true if user has all rights except 'delete'
     */
    public boolean isFullRight() {
        return canTakeSnapshot() && canView() && canEdit() && canList();
    }

    /**
     * Validate if user has read only rights on this camera
     */
    public boolean isReadOnly() {
        return canTakeSnapshot() && canList() && !canEdit() && !canView();
    }

    @Override
    public String toString() {
        return rightsString;
    }
}
