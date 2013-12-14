package Runtime;

/**
 *
 * @author Ryan Kasprzyk
 *
 * The Symbol object holds all relevant information about a given variable. It
 * also handles changing held values and type checking.
 */

public class Symbol {

    String n, t, l, v;

    /**
     *
     * @param name
     * @param type
     * @param length
     * @param value
     */
    public Symbol(String name, String type, String length, String value) {
        n = name;
        t = type;
        l = length;
        v = value;
    }

    public int changeValue(String newVal) {
        if (typeCheck(newVal)) {
            v = newVal;
        } else {
            return -1;
        }
        return 0;
    }

    public int changeSize(String newSize) {
        if (Integer.parseInt(newSize) < v.length()) {
            return -3;
        } else {
            l = newSize;
        }
        return 0;
    }

    public int changeType(String newType) {
        if (t.matches("POSITIVE")) {
            if (newType.matches("NUMBER")) {
                t = newType;
            } else if (newType.matches("VARCHAR2")) {
                t = newType;
            } else if (newType.matches("CHAR") && v.length() == 1) {
                t = newType;
            } else {
                return -1;
            }
        } else if (t.matches("NUMBER")) {
            if (newType.matches("POSITIVE")) {
                if (v.contains("[-]")) {
                    return -1;
                } else {
                    t = newType;
                }
            } else if (newType.matches("VARCHAR")) {
                t = newType;
            } else if (newType.matches("CHAR") && v.length() == 1) {
                t = newType;
            } else {
                return -1;
            }
        } else if (t.matches("CHAR")) {
            if (newType.matches("VARCHAR2")) {
                t = newType;
            } else if (newType.matches("NUMBER")) {
                t = newType;
                v = String.valueOf((int) (v.charAt(0)));
            } else if (newType.matches("POSITIVE")) {
                t = newType;
                v = String.valueOf((int) (v.charAt(0)));
            } else {
                return -1;
            }
        } else if (t.matches("VARCHAR")) {
            if (newType.matches("CHAR") && v.length() == 1) {
                l = "";
                t = newType;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
        return 0;
    }

    public boolean typeCheck(String newVal) {
        if (t.equals("NUMBER")) {
            try {
                Integer.parseInt(newVal);
                if (newVal.length() > Integer.parseInt(l)) {
                    return false;
                } else {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (t.equals("POSITIVE")) {
            try {
                Integer.parseInt(newVal);
                if (newVal.contains("[-]")) {
                    return false;
                } else {
                    if (newVal.length() > Integer.parseInt(l)) {
                        return false;
                    } else {
                        return true;
                    }
                }

            } catch (NumberFormatException e) {
                return false;
            }
        } else if (t.equals("VARCHAR2")) {
            if (newVal.length() > Integer.parseInt(l)) {
                return false;
            } else {
                return true;
            }
        } else if (t.equals("CHAR")) {
            if (newVal.length() > 1) {
                return false;
            } else {
                return true;
            }
        } else if (t.equals("BOOLEAN")) {
            if (newVal.matches("TRUE") || newVal.matches("FALSE")) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}