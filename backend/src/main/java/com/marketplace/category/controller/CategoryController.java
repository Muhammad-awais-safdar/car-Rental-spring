package com.marketplace.category.controller;

import com.marketplace.category.dto.CategoryDTO;
import com.marketplace.category.service.CategoryService;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Public endpoints
    @GetMapping("/makes")
    public ResponseEntity<ApiResponse<List<CategoryDTO.MakeResponse>>> getAllMakes() {
        List<CategoryDTO.MakeResponse> makes = categoryService.getAllMakes();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Makes retrieved successfully", makes));
    }

    @GetMapping("/makes/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO.MakeWithModelsResponse>> getMakeWithModels(
            @PathVariable Long id) {
        CategoryDTO.MakeWithModelsResponse make = categoryService.getMakeWithModels(id);
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Make details retrieved successfully", make));
    }

    @GetMapping("/makes/{id}/models")
    public ResponseEntity<ApiResponse<List<CategoryDTO.ModelResponse>>> getModelsByMake(
            @PathVariable Long id) {
        List<CategoryDTO.ModelResponse> models = categoryService.getModelsByMake(id);
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Models retrieved successfully", models));
    }

    // Admin endpoints
    @PostMapping("/admin/makes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDTO.MakeResponse>> createMake(
            @Valid @RequestBody CategoryDTO.MakeRequest request) {
        CategoryDTO.MakeResponse make = categoryService.createMake(request);
        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Make created successfully", make),
                HttpStatus.CREATED);
    }

    @PutMapping("/admin/makes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDTO.MakeResponse>> updateMake(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.MakeRequest request) {
        CategoryDTO.MakeResponse make = categoryService.updateMake(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Make updated successfully", make));
    }

    @DeleteMapping("/admin/makes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMake(@PathVariable Long id) {
        categoryService.deleteMake(id);
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Make deleted successfully", null));
    }

    @PostMapping("/admin/makes/{makeId}/models")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDTO.ModelResponse>> createModel(
            @PathVariable Long makeId,
            @Valid @RequestBody CategoryDTO.ModelRequest request) {
        CategoryDTO.ModelResponse model = categoryService.createModel(makeId, request);
        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Model created successfully", model),
                HttpStatus.CREATED);
    }

    @PutMapping("/admin/models/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDTO.ModelResponse>> updateModel(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.ModelRequest request) {
        CategoryDTO.ModelResponse model = categoryService.updateModel(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Model updated successfully", model));
    }

    @DeleteMapping("/admin/models/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteModel(@PathVariable Long id) {
        categoryService.deleteModel(id);
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Model deleted successfully", null));
    }
}
