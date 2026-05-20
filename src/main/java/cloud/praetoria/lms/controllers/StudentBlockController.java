package cloud.praetoria.lms.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.AssignBlocksRequest;
import cloud.praetoria.lms.dtos.BlockResponse;
import cloud.praetoria.lms.dtos.PromoRequest;
import cloud.praetoria.lms.dtos.PromoResponse;
import cloud.praetoria.lms.dtos.UserResponse;
import cloud.praetoria.lms.services.BlockService;
import cloud.praetoria.lms.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/blocks/student")
@RequiredArgsConstructor
public class StudentBlockController {
	
	private BlockService blockService;
	private UserService  userService;
	
	@GetMapping
	@Operation(summary = "Récupérer tous les blocs")
	public ResponseEntity<ApiResponse<List<BlockResponse>>> getAllBlocks() {
	    List<BlockResponse> blocks = blockService.getAllBlocks();
	    return ResponseEntity.ok(ApiResponse.success(blocks));
	} 

	@PutMapping("/{userId}/blocks")
	@Operation(summary ="assugne un block un un etudiant")
	public ResponseEntity<ApiResponse<UserResponse>> assignBlocks(
			@PathVariable Long userId,
			@Valid @RequestBody AssignBlocksRequest request){
		UserResponse userResponse = userService.assignBlocks(userId, request);
		return ResponseEntity.ok(ApiResponse.success(userResponse, "blocs assignés avec succès"));
	}
			
			
	
	 
	

	
}
