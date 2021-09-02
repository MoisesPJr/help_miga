package com.example.helpmiga.data.viewModel

import androidx.lifecycle.*
import com.example.helpmiga.data.repository.ContatoRepository
import com.example.helpmiga.data.model.Contato
import com.example.helpmiga.data.model.Usuario
import com.example.helpmiga.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    fun insert(usuario: Usuario) = viewModelScope.launch {
        repository.insert(usuario)
    }

    fun apagarContato(usuario: Usuario) = viewModelScope.launch {
        repository.apagarUsuario(usuario)
    }
}

class UsuarioViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContatoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}