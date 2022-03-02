package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.dto.ContractAttachmentDTO;
import io.legacyfighter.cabs.dto.ContractDTO;
import io.legacyfighter.cabs.entity.Contract;
import io.legacyfighter.cabs.entity.ContractAttachmentData;
import io.legacyfighter.cabs.repository.ContractAttachmentDataRepository;
import io.legacyfighter.cabs.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractAttachmentDataRepository contractAttachmentDataRepository;

    @Transactional
    public Contract createContract(ContractDTO contractDTO) {
        int partnerContractsCount = contractRepository.findByPartnerName(contractDTO.getPartnerName()).size();
        Contract contract = Contract.from(contractDTO.getPartnerName(), contractDTO.getSubject(), partnerContractsCount);
        return contractRepository.save(contract);
    }

    @Transactional
    public void acceptContract(Long id) {
        Contract contract = find(id);
        contract.accept();
    }

    @Transactional
    public void rejectContract(Long id) {
        Contract contract = find(id);
        contract.reject();
    }


    @Transactional
    public void rejectAttachment(Long attachmentId) {
        Contract contract = contractRepository.findByAttachmentId(attachmentId);
        UUID attachmentNo = contractRepository.findAttachmentNoById(attachmentId);
        contract.rejectAttachment(attachmentNo);
    }

    @Transactional
    public void acceptAttachment(Long attachmentId) {
        Contract contract = contractRepository.findByAttachmentId(attachmentId);
        UUID attachmentNo = contractRepository.findAttachmentNoById(attachmentId);
        contract.acceptAttachment(attachmentNo);
    }

    @Transactional
    public Contract find(Long id) {
        Contract contract = contractRepository.getOne(id);
        if (contract == null) {
            throw new IllegalStateException("Contract does not exist");
        }
        return contract;
    }

    @Transactional
    public ContractDTO findDto(Long id) {
        Contract contract = find(id);
        Set<ContractAttachmentData> attachmentData = contractAttachmentDataRepository.findByAttachmentNoIn(contract.getAttachmentNos());
        return new ContractDTO(contract, attachmentData);
    }

    @Transactional
    public ContractAttachmentDTO proposeAttachment(Long contractId, ContractAttachmentDTO contractAttachmentDTO) {
        Contract contract = find(contractId);
        UUID attachmentId = contract.proposeAttachment().getAttachmentNo();
        ContractAttachmentData contractAttachmentData = ContractAttachmentData.from(attachmentId, contractAttachmentDTO.getData());
        Contract saved = contractRepository.save(contract);
        return new ContractAttachmentDTO(saved.findAttachment(attachmentId), contractAttachmentDataRepository.save(contractAttachmentData));
    }

    @Transactional
    public void removeAttachment(Long contractId, Long attachmentId) {
        Contract contract = find(contractId);
        UUID attachmentNo = contractRepository.findAttachmentNoById(attachmentId);
        contract.removeAttachment(attachmentNo);
        contractAttachmentDataRepository.deleteByAttachmentNo(attachmentNo);
    }
}
