package cloud.praetoria.lms.services.impl;

import cloud.praetoria.lms.dtos.BlockRequest;
import cloud.praetoria.lms.dtos.BlockResponse;
import cloud.praetoria.lms.entities.Block;
import cloud.praetoria.lms.exceptions.DuplicateResourceException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.BlockRepository;
import cloud.praetoria.lms.services.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;

    @Override
    public List<BlockResponse> getAllBlocks() {
        return blockRepository.findAll().stream()
                .map(BlockResponse::fromEntity)
                .toList();
    }

    @Override
    public BlockResponse getBlockById(Long id) {
        Block block = blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloc non trouvé"));
        return BlockResponse.fromEntityWithModules(block);
    }

    @Override
    @Transactional
    public BlockResponse createBlock(BlockRequest request) {
        if (blockRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Le nom du bloc existe déjà");
        }
        Block block = Block.builder()
                .name(request.getName())
                .description(request.getDescription())
                .cover(request.getCover())
                .build();
        return BlockResponse.fromEntity(blockRepository.save(block));
    }

    @Override
    @Transactional
    public BlockResponse updateBlock(Long id, BlockRequest request) {
        Block block = blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloc non trouvé"));
        if (blockRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Le nom du bloc existe déjà");
        }
        block.setName(request.getName());
        block.setDescription(request.getDescription());
        block.setCover(request.getCover());
        return BlockResponse.fromEntity(blockRepository.save(block));
    }

    @Override
    @Transactional
    public void deleteBlock(Long id) {
        if (!blockRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bloc non trouvé");
        }
        blockRepository.deleteById(id);
    }
}