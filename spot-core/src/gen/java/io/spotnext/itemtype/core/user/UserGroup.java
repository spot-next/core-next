/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.user;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.ItemType;

import io.spotnext.itemtype.core.user.PrincipalGroup;

import java.io.Serializable;

import java.lang.String;


/**
 * The base type all user group-like structures.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "usergroup")
public class UserGroup extends PrincipalGroup {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "usergroup";
}
