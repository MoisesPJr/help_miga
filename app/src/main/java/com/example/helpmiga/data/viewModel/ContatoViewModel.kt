package com.example.helpmiga.data.viewModel

import androidx.lifecycle.*
import com.example.helpmiga.data.repository.ContatoRepository
import com.example.helpmiga.ui.Contato
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContatoViewModel(private val repository: ContatoRepository) : ViewModel() {

    fun insert(contato: Contato) = viewModelScope.launch {
        repository.insert(contato)
    }

     fun listaContatos() : List<Contato>{
      return repository.getallContatos()
   }


}

class ContatoViewModelFactory(private val repository: ContatoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContatoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContatoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}