package com.sdzee.servlets;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Download extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lecture du paramètre 'chemin' passé à la servlet via la déclaration dans le web.xml
        String chemin = this.getServletConfig().getInitParameter("chemin");
        // Récupération du chemin du fichier demandé au sein de l'URL de la requête
        String fichierRequis = request.getPathInfo();
        // Vérifie qu'un fichier a bien été fourni
        if (fichierRequis == null || "/".equals(fichierRequis)) {
            /* Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas */
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //L'étape suivante consiste à contrôler le nom du fichier transmis et à vérifier si un tel fichier existe :

        // Décode le nom de fichier récupéré, susceptible de contenir des espaces et autres caractères spéciaux,
        // et prépare l'objet File
        fichierRequis = URLDecoder.decode(fichierRequis, "UTF-8");
        File fichier = new File(chemin, fichierRequis);

        // Vérifie que le fichier existe bien
        if (!fichier.exists()) {
            // Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }
}