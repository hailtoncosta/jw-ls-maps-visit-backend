package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.model.EnderecoCompartilhado;
import com.visit.jw_ls_maps_visit.repository.EnderecoCompartilhadoRepository;
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;
import com.visit.jw_ls_maps_visit.service.EnderecoCompartilhadoService;

@RestController 
@RequestMapping("/api/endereco-compartilhado")
public class EnderecoCompartilhadoController {
    
    private final EnderecoCompartilhadoRepository enderecoRepository;
    private final EnderecoRepository addresses;
    private final CurrentUser currentUser;
    private final EnderecoCompartilhadoService enderecoService;

    public EnderecoCompartilhadoController(EnderecoCompartilhadoRepository enderecoRepo, EnderecoCompartilhadoService endService,
            EnderecoRepository addresses, CurrentUser currentUser) {
        enderecoRepository = enderecoRepo;
        enderecoService = endService;
        this.addresses = addresses;
        this.currentUser = currentUser;
    }

    @GetMapping("/listAll")
    public List<EnderecoCompartilhado> list() {
        return enderecoRepository.findAll().stream().filter(this::inCircuit).toList();
    }

    @GetMapping("/findById/{id}")
    public EnderecoCompartilhado getById(@PathVariable UUID id) {
        var share = enderecoRepository.findById(id).orElseThrow();
        requireCircuit(share);
        return share;
    }

    @PostMapping("/save")
    public EnderecoCompartilhado create(@RequestBody EnderecoCompartilhado endereco) {
        requireCircuit(endereco);
        return enderecoService.compartilhado(endereco);
    }

    @PutMapping("/update/{id}")
    public EnderecoCompartilhado update(@PathVariable UUID id, @RequestBody EnderecoCompartilhado endereco) {
        requireCircuit(getById(id));
        requireCircuit(endereco);
        endereco.setId(id);
        return enderecoRepository.save(endereco);
    }

    @PostMapping("/{id}/retake")
    public EnderecoCompartilhado retake(@PathVariable UUID id) {
        requireCircuit(getById(id));
        return enderecoService.retake(id);
    }

    @DeleteMapping("/by-address/{addressId}")
    public void releaseByAddress(@PathVariable UUID addressId) {
        if (currentUser.is("superintendente") && (currentUser.get().getCircuitoId() == null
                || addresses.findById(addressId).map(a -> !currentUser.get().getCircuitoId().equals(a.getCircuitoId())).orElse(true)))
            throw new SecurityException("Endereço fora do circuito");
        enderecoRepository.findAll().stream()
            .filter(x -> addressId.equals(x.getAddressId()))
            .forEach(x -> enderecoService.release(x.getId()));
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        requireCircuit(getById(id));
        enderecoRepository.deleteById(id);
    }

    private boolean inCircuit(EnderecoCompartilhado share) {
        if (!currentUser.is("superintendente")) return true;
        var circuitoId = currentUser.get().getCircuitoId();
        return circuitoId != null && share.getAddressId() != null && addresses.findById(share.getAddressId())
            .map(a -> circuitoId.equals(a.getCircuitoId())).orElse(false);
    }

    private void requireCircuit(EnderecoCompartilhado share) {
        if (!inCircuit(share)) throw new SecurityException("Compartilhamento fora do circuito");
    }

}
