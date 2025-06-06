package inventorymanagementsystem.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity

public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true) // Added unique here for clarity
    private String name;

    // Removed the owner field entirely

    public Category() {}

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(Long id) {
        this(id, null);
    }

    public Category(String name) {
        this(null, name);
    }

    // Removed the owner parameter from all constructors

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Removed getOwner() and setOwner() methods

    public CategoryForm toForm() {
        return new CategoryForm(name);
    }
}