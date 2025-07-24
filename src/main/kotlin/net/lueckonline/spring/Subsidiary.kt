package net.lueckonline.spring

import jakarta.persistence.*
import java.util.UUID

@Entity
class Subsidiary(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var address: String,

    @ManyToOne
    @JoinColumn(name = "library_id", nullable = false, updatable = false)
    var library: Library? = null
)
