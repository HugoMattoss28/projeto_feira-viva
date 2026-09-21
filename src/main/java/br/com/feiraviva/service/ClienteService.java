package br.com.feiraviva.service;

import br.com.feiraviva.dto.*;
import br.com.feiraviva.exception.*;
import br.com.feiraviva.model.Cliente;
import br.com.feiraviva.model.Endereco;
import br.com.feiraviva.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponseDTO criar(ClienteDTO dto) {

        if (clienteRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException(
                    "E-mail já cadastrado: " + dto.email()
            );
        }

        var cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setSenhaHash(dto.senha()); // provisório
        cliente.setTelefone(dto.telefone());

        // NOVA PARTE: Processar os endereços recebidos na requisição
        if (dto.enderecos() != null && !dto.enderecos().isEmpty()) {
            for (var enderecoDto : dto.enderecos()) {
                var endereco = new Endereco();
                endereco.setCep(enderecoDto.cep());
                endereco.setLogradouro(enderecoDto.logradouro());
                endereco.setNumero(enderecoDto.numero());
                endereco.setComplemento(enderecoDto.complemento());
                endereco.setBairro(enderecoDto.bairro());
                endereco.setCidade(enderecoDto.cidade());
                endereco.setUf(enderecoDto.uf());

                // Estabelece a ligação de "quem é o dono deste endereço"
                endereco.setCliente(cliente);

                cliente.getEnderecos().add(endereco);
            }
        }

        return paraResponse(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscar(Long id) {
        return paraResponse(buscarCliente(id));
    }

    @Transactional
    public void adicionarEndereco(Long clienteId, EnderecoDTO dto) {

        var cliente = buscarCliente(clienteId);

        var endereco = new Endereco();
        endereco.setCep(dto.cep());
        endereco.setLogradouro(dto.logradouro());
        endereco.setNumero(dto.numero());
        endereco.setComplemento(dto.complemento());
        endereco.setBairro(dto.bairro());
        endereco.setCidade(dto.cidade());
        endereco.setUf(dto.uf());
        endereco.setCliente(cliente);

        cliente.getEnderecos().add(endereco);
    }

    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente não encontrado"
                        )
                );
    }

    private ClienteResponseDTO paraResponse(Cliente c) {
        return new ClienteResponseDTO(
                c.getId(),
                c.getNome(),
                c.getEmail(),
                c.getTelefone(),
                c.getEnderecos().stream()
                        .map(e -> new EnderecoDTO(
                                e.getCep(),
                                e.getLogradouro(),
                                e.getNumero(),
                                e.getComplemento(),
                                e.getBairro(),
                                e.getCidade(),
                                e.getUf()
                        ))
                        .toList()
        );
    }
}