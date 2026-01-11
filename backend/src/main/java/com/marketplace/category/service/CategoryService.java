package com.marketplace.category.service;

import com.marketplace.category.dto.CategoryDTO;
import com.marketplace.category.entity.Make;
import com.marketplace.category.entity.Model;
import com.marketplace.category.repository.MakeRepository;
import com.marketplace.category.repository.ModelRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private MakeRepository makeRepository;

    @Autowired
    private ModelRepository modelRepository;

    // Make operations
    public List<CategoryDTO.MakeResponse> getAllMakes() {
        return makeRepository.findAll().stream()
                .map(this::convertMakeToResponse)
                .collect(Collectors.toList());
    }

    public CategoryDTO.MakeWithModelsResponse getMakeWithModels(Long makeId) {
        Make make = makeRepository.findById(makeId)
                .orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        List<CategoryDTO.ModelResponse> models = modelRepository.findByMakeId(makeId).stream()
                .map(this::convertModelToResponse)
                .collect(Collectors.toList());

        return CategoryDTO.MakeWithModelsResponse.builder()
                .id(make.getId())
                .name(make.getName())
                .logoUrl(make.getLogoUrl())
                .models(models)
                .build();
    }

    public CategoryDTO.MakeResponse createMake(CategoryDTO.MakeRequest request) {
        if (makeRepository.existsByName(request.getName())) {
            throw new BadRequestException("Make already exists");
        }

        Make make = Make.builder()
                .name(request.getName())
                .logoUrl(request.getLogoUrl())
                .build();

        make = makeRepository.save(make);
        return convertMakeToResponse(make);
    }

    public CategoryDTO.MakeResponse updateMake(Long makeId, CategoryDTO.MakeRequest request) {
        Make make = makeRepository.findById(makeId)
                .orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        if (!make.getName().equals(request.getName()) && makeRepository.existsByName(request.getName())) {
            throw new BadRequestException("Make name already exists");
        }

        make.setName(request.getName());
        make.setLogoUrl(request.getLogoUrl());

        make = makeRepository.save(make);
        return convertMakeToResponse(make);
    }

    public void deleteMake(Long makeId) {
        if (!makeRepository.existsById(makeId)) {
            throw new ResourceNotFoundException("Make not found");
        }
        makeRepository.deleteById(makeId);
    }

    // Model operations
    public List<CategoryDTO.ModelResponse> getModelsByMake(Long makeId) {
        if (!makeRepository.existsById(makeId)) {
            throw new ResourceNotFoundException("Make not found");
        }

        return modelRepository.findByMakeId(makeId).stream()
                .map(this::convertModelToResponse)
                .collect(Collectors.toList());
    }

    public CategoryDTO.ModelResponse createModel(Long makeId, CategoryDTO.ModelRequest request) {
        Make make = makeRepository.findById(makeId)
                .orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        if (modelRepository.existsByMakeIdAndName(makeId, request.getName())) {
            throw new BadRequestException("Model already exists for this make");
        }

        Model model = Model.builder()
                .make(make)
                .name(request.getName())
                .build();

        model = modelRepository.save(model);
        return convertModelToResponse(model);
    }

    public CategoryDTO.ModelResponse updateModel(Long modelId, CategoryDTO.ModelRequest request) {
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        if (!model.getName().equals(request.getName()) &&
                modelRepository.existsByMakeIdAndName(model.getMake().getId(), request.getName())) {
            throw new BadRequestException("Model name already exists for this make");
        }

        model.setName(request.getName());
        model = modelRepository.save(model);
        return convertModelToResponse(model);
    }

    public void deleteModel(Long modelId) {
        if (!modelRepository.existsById(modelId)) {
            throw new ResourceNotFoundException("Model not found");
        }
        modelRepository.deleteById(modelId);
    }

    private CategoryDTO.MakeResponse convertMakeToResponse(Make make) {
        return CategoryDTO.MakeResponse.builder()
                .id(make.getId())
                .name(make.getName())
                .logoUrl(make.getLogoUrl())
                .modelCount(make.getModels() != null ? make.getModels().size() : 0)
                .createdAt(make.getCreatedAt())
                .build();
    }

    private CategoryDTO.ModelResponse convertModelToResponse(Model model) {
        return CategoryDTO.ModelResponse.builder()
                .id(model.getId())
                .makeId(model.getMake().getId())
                .makeName(model.getMake().getName())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .build();
    }
}
