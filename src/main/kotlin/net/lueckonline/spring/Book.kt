package net.lueckonline.spring

import jakarta.persistence.*
import java.util.Objects
import java.util.UUID

@Entity
class Book(
    @Id
    val id: UUID = UUID.randomUUID(),

    val title: String,

    val genre: Genre,

    @ManyToOne
    @JoinColumn(name = "library_id")
    var library: Library? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        return title == other.title
    }

    override fun hashCode(): Int {
        return Objects.hash(title)
    }
}
