package com.gestionstages.security;

import com.gestionstages.model.entity.Utilisateur;
import com.gestionstages.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .authorities(getAuthorities(utilisateur))
                .accountExpired(false)
                .accountLocked(!utilisateur.getActif())
                .credentialsExpired(false)
                .disabled(!utilisateur.getActif())
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Utilisateur utilisateur) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())
        );
    }
}