package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.time.Instant;
import java.util.UUID;

@Entity
public class ContractAttachmentData extends BaseEntity {

    ContractAttachmentData() {}

    @Column(nullable = false)
    private UUID attachmentNo;

    @Lob
    @Column(name = "data", columnDefinition="BLOB")
    private byte[] data;

    @Column(nullable = false)
    private Instant creationDate = Instant.now();

    public ContractAttachmentData(UUID attachmentNo, byte[] data) {
        this.attachmentNo = attachmentNo;
        this.data = data;
    }

    public static ContractAttachmentData from(UUID attachmentId, byte[] data) {
        return new ContractAttachmentData(attachmentId, data);
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public byte[] getData() {
        return data;
    }

    public UUID getAttachmentNo() {
        return attachmentNo;
    }
}
