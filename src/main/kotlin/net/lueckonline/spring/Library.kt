package net.lueckonline.spring

import jakarta.persistence.*
import java.util.UUID

@Entity
class Library(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String,

    @OneToMany(mappedBy = "library", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val books: MutableSet<Book> = mutableSetOf()
)
