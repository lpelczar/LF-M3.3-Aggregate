package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.dto.ContractAttachmentDTO;
import io.legacyfighter.cabs.dto.ContractDTO;
import io.legacyfighter.cabs.entity.Contract;
import io.legacyfighter.cabs.entity.ContractAttachment;
import io.legacyfighter.cabs.repository.ContractAttachmentRepository;
import io.legacyfighter.cabs.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractAttachmentRepository contractAttachmentRepository;

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
        ContractAttachment contractAttachment = contractAttachmentRepository.getOne(attachmentId);
        contractAttachment.reject();
    }

    @Transactional
    public void acceptAttachment(Long attachmentId) {
        ContractAttachment contractAttachment = contractAttachmentRepository.getOne(attachmentId);
        contractAttachment.accept();
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
        return new ContractDTO(find(id));
    }

    @Transactional
    public ContractAttachmentDTO proposeAttachment(Long contractId, ContractAttachmentDTO contractAttachmentDTO) {
        Contract contract = find(contractId);
        ContractAttachment proposed = contract.proposeAttachment(contractAttachmentDTO.getData());
        contractAttachmentRepository.save(proposed);
        return new ContractAttachmentDTO(proposed);
    }

    @Transactional
    public void removeAttachment(Long contractId, Long attachmentId) {
        //TODO sprawdzenie czy nalezy do kontraktu (JIRA: II-14455)
        contractAttachmentRepository.deleteById(attachmentId);
    }
}
