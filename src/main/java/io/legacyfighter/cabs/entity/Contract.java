package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class Contract extends BaseEntity {


    public enum Status {
        NEGOTIATIONS_IN_PROGRESS, REJECTED, ACCEPTED
    }

    Contract() {}

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.JOIN)
    private Set<ContractAttachment> attachments = new HashSet<>();

    private String partnerName;

    private String subject;

    @Column(nullable = false)
    private Instant creationDate = Instant.now();

    private Instant acceptedAt;

    private Instant rejectedAt;

    private Instant changeDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NEGOTIATIONS_IN_PROGRESS;

    @Column(nullable = false)
    private String contractNo;

    Contract(String partnerName, String subject, String contractNo) {
        this.partnerName = partnerName;
        this.subject = subject;
        this.contractNo = contractNo;
    }

    public static Contract from(String partnerName, String subject, int partnerContractsCount) {
        int createdContractCount = partnerContractsCount + 1;
        String contractNo = "C/" + createdContractCount + "/" + partnerName;
        return new Contract(partnerName, subject, contractNo);
    }

    public void accept() {
        if (attachments.stream().allMatch(a -> a.getStatus().equals(ContractAttachment.Status.ACCEPTED_BY_BOTH_SIDES))) {
            status = Status.ACCEPTED;
        } else {
            throw new IllegalStateException("Not all attachments accepted by both sides");
        }
    }

    public void reject() {
        status = Status.REJECTED;
    }

    public void acceptAttachment(UUID attachmentNo) {
        ContractAttachment contractAttachment = findAttachment(attachmentNo);
        contractAttachment.accept();
    }

    public void rejectAttachment(UUID attachmentNo) {
        ContractAttachment contractAttachment = findAttachment(attachmentNo);
        contractAttachment.reject();
    }

    public ContractAttachment proposeAttachment() {
        ContractAttachment contractAttachment = ContractAttachment.proposed(this);
        this.attachments.add(contractAttachment);
        return contractAttachment;
    }

    public ContractAttachment findAttachment(UUID attachmentNo) {
        return attachments.stream()
                .filter(attachment -> attachment.getAttachmentNo().equals(attachmentNo))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Attachment not found"));
    }

    public void removeAttachment(UUID attachmentNo) {
        attachments.removeIf(attachment -> attachment.getAttachmentNo().equals(attachmentNo));
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public Instant getRejectedAt() {
        return rejectedAt;
    }

    public Instant getChangeDate() {
        return changeDate;
    }

    public Status getStatus() {
        return status;
    }

    public String getContractNo() {
        return contractNo;
    }

    public Set<ContractAttachment> getAttachments() {
        return Collections.unmodifiableSet(attachments);
    }

    public Set<UUID> getAttachmentNos() {
        return attachments.stream().map(ContractAttachment::getAttachmentNo).collect(Collectors.toSet());
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Contract))
            return false;

        Contract other = (Contract) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
