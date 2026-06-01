package com.library.api.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A magazine — a {@link LibraryItem} identified by its edition number.
 *
 * @author stewicca
 * @version 1.0
 */
@Entity
@DiscriminatorValue("MAGAZINE")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Magazine extends LibraryItem {

    private int edition;

    @Override
    public String describe() {
        return "Magazine: " + getTitle() + " (edition " + edition + ")";
    }

    @Override
    public String itemType() {
        return "MAGAZINE";
    }
}
