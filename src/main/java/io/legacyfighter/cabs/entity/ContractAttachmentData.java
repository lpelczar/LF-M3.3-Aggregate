package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class ContractAttachmentData extends BaseEntity {

    ContractAttachmentData() {}

    @Column(nullable = false)
    private Long attachmentId;

    @Lob
    @Column(name = "data", columnDefinition="BLOB")
    private byte[] data;

    public ContractAttachmentData(Long attachmentId, byte[] data) {
        this.attachmentId = attachmentId;
        this.data = data;
    }

    public static ContractAttachmentData from(Long attachmentId, byte[] data) {
        return new ContractAttachmentData(attachmentId, data);
    }

    public byte[] getData() {
        return data;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }
}
