package com.library.api.entity;

import com.library.api.exception.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base of everything that can sit on a library shelf and be borrowed.
 *
 * <p>Demonstrates OOP fundamentals required by the assessment:
 * <ul>
 *   <li><b>Inheritance</b> — {@link Book} and {@link Magazine} extend this class.</li>
 *   <li><b>Polymorphism</b> — {@link #describe()} and {@link #itemType()} are abstract and
 *       implemented differently by each subclass.</li>
 *   <li><b>Encapsulation</b> — {@code id} and {@code availableCopies} are {@code private}
 *       (the stock is only ever changed through {@link #borrowOne()} / {@link #returnOne()}),
 *       while shared state ({@code title}, {@code author}) is {@code protected}.</li>
 * </ul>
 *
 * @author stewicca
 * @version 1.0
 */
@Entity
@Table(name = "library_items", indexes = @Index(name = "idx_item_title", columnList = "title"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class LibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;                       // private: identity is assigned by the persistence layer

    @Column(nullable = false)
    protected String title;                  // protected: shared, inherited state

    protected String author;

    @Column(nullable = false)
    private int availableCopies;             // private: mutated only through borrowOne()/returnOne()

    /**
     * Polymorphic: each concrete item renders its own human-readable description.
     *
     * @return a one-line description of this item
     */
    public abstract String describe();

    /**
     * Polymorphic discriminator used when mapping to a response DTO.
     *
     * @return the item type, e.g. {@code "BOOK"} or {@code "MAGAZINE"}
     */
    public abstract String itemType();

    /**
     * Borrow a single copy, enforcing the "no copies left" business rule.
     *
     * @throws BusinessRuleException when no copies are available
     */
    public void borrowOne() {
        if (availableCopies <= 0) {
            throw new BusinessRuleException("No copies available for: " + title);
        }
        availableCopies--;
    }

    /** Return a single copy back to the shelf. */
    public void returnOne() {
        availableCopies++;
    }
}
