package net.lueckonline.spring

import jakarta.persistence.*
import java.util.UUID

@Entity
class Book(
    @Id
    val id: UUID = UUID.randomUUID(),
    var title: String,

    @ManyToOne
    @JoinColumn(name = "library_id")
    var library: Library? = null
)

