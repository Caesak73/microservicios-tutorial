package com.usuario.service.servicio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.usuario.service.entidades.Usuario;
import com.usuario.service.feignclients.CarroFeignClient;
import com.usuario.service.feignclients.MotoFeignClient;
import com.usuario.service.modelos.Carro;
import com.usuario.service.modelos.Moto;
import com.usuario.service.repositorio.UsuarioRepository;

@Service
@Transient
public class UsuarioService {

	@Autowired
	public UsuarioRepository usuarioRepository;

	@Autowired
	public RestTemplate restTemplate;

	@Autowired
	public CarroFeignClient carroFeignClient;

	@Autowired
	public MotoFeignClient motoFeignClient;

	public List<Carro> getCarros(int usuarioId) {
		List<Carro> carros = restTemplate.getForObject("http://localhost:8002/carro/usuario/" + usuarioId, List.class);
		return carros;
	}

	public List<Moto> getMotos(int usuarioId) {
		List<Moto> motos = restTemplate.getForObject("http://moto-service/moto/usuario/" + usuarioId, List.class);
		return motos;
	}
	
	public Map<String,Object> getUsuarioAndVehiculos(int usuarioId){
		Map<String,Object> resultado = new HashMap<>();
		Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
		
		if(usuario == null) {
			resultado.put("Mensaje", "El usuario no existe");
			return resultado;
		}
		
		resultado.put("Usuario", usuario);
		List<Carro> carros = carroFeignClient.getCarros(usuarioId);
		if(carros.isEmpty()) {
			resultado.put("Carros", "El usuario no tiene carros");
		}
		else {
			resultado.put("Carros", carros);
		}
		
		List<Moto> motos = motoFeignClient.getMotos(usuarioId);
		if(motos.isEmpty()) {
			resultado.put("Motos", "El usuario no tiene motos");
		}
		else {
			resultado.put("Motos", carros);
		}
		return resultado;
	}

	public Carro saveCarro(int usuarioId, Carro carro) {
		carro.setUsuarioId(usuarioId);
		Carro nuevoCarro = carroFeignClient.save(carro);

		return nuevoCarro;
	}

	public Moto saveMoto(int usuarioId, Moto moto) {
		moto.setUsuarioId(usuarioId);
		Moto nuevoMoto = motoFeignClient.save(moto);

		return nuevoMoto;
	}

	public List<Usuario> getAll() {
		return usuarioRepository.findAll();
	}

	public Usuario getUsuarioById(int id) {
		return usuarioRepository.findById(id).orElse(null);
	}

	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);

	}
}
