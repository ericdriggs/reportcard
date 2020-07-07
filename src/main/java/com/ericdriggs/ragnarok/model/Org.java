package com.ericdriggs.ragnarok.model;

import com.ericdriggs.ragnarok.interfaces.HasNameId;
import lombok.Data;

/**
 * An organization (has repositories)
 */
@Data
public class Org implements HasNameId {
    private Long id;
    private String name;
}
