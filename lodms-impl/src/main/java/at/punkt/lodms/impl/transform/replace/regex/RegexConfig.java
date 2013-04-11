/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.replace.regex;

import java.io.Serializable;

/**
 *
 * @author kreisera
 */
public class RegexConfig implements Serializable {

    private String regex = "";
    private String replacement = "";
    private boolean replaceAll = true;
    private ValueType valueType = ValueType.LITERAL;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public boolean isReplaceAll() {
        return replaceAll;
    }

    public void setReplaceAll(boolean replaceAll) {
        this.replaceAll = replaceAll;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }
}