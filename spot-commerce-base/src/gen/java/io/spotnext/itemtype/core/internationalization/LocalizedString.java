/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.internationalization;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.types.Item;
import io.spotnext.core.types.Localizable;

import java.io.Serializable;

import java.lang.String;


/**
 * This type can be used to store localized strings.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "localizedstring")
public class LocalizedString extends Item implements Localizable<String> {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "localizedstring";
    public static final String PROPERTY_EN = "en";
    public static final String PROPERTY_EN__G_B = "en_GB";
    public static final String PROPERTY_DE = "de";
    public static final String PROPERTY_DE__D_E = "de_DE";
    public static final String PROPERTY_FR = "fr";
    public static final String PROPERTY_FR__F_R = "fr_FR";
    public static final String PROPERTY_ES = "es";
    public static final String PROPERTY_ES__E_S = "es_ES";
    public static final String PROPERTY_IT = "it";
    public static final String PROPERTY_IT__I_T = "it_IT";
    public static final String PROPERTY_JA = "ja";
    public static final String PROPERTY_JA__J_P = "ja_JP";
    @Property
    protected String en;
    @Property
    protected String en_GB;
    @Property
    protected String de;
    @Property
    protected String de_DE;
    @Property
    protected String fr;
    @Property
    protected String fr_FR;
    @Property
    protected String es;
    @Property
    protected String es_ES;
    @Property
    protected String it;
    @Property
    protected String it_IT;
    @Property
    protected String ja;
    @Property
    protected String ja_JP;
}
