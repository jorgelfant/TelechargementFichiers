package com.sdzee.servlets;

//import java.nio.charset.StandardCharsets; permet de  enc: StandardCharsets.UTF_8.toString()

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Download extends HttpServlet {

    private static final int TAILLE_TAMPON = 10240;// 10 ko

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lecture du paramètre 'chemin' passé à la servlet via la déclaration dans le web.xml
        String chemin = this.getServletConfig().getInitParameter("chemin");
        // Récupération du chemin du fichier demandé au sein de l'URL de la requête
        String fichierRequis = request.getPathInfo();
        // Vérifie qu'un fichier a bien été fourni
        if (fichierRequis == null) {
            /* Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas */
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;//return ici permet de sortir de la méthode sans exécuter ce qui vient après cette mot clé
        }          // Use the return keyword to exit from a method.

        /*
        Any method declared void doesn't return a value. It does not need to contain a return statement, but it may do so.
        In such a case, a return statement can be used to branch out of a control flow block and exit the method and is
        simply used like this:
                                   return;

        break permet de sortir de par exemple une boucle mais on continue l'excution alors que return
        permet de salir de la méthode complète.

        If you are deeply in recursion inside recursive method, throwing and catching exception may be an option.

        Unlike Return that returns only one level up, exception would break out of recursive method as well into
        the code that initially called it, where it can be catched.
        */

        //--------------------------------------------------------------------------------------------------------------
        // L'étape suivante consiste à contrôler le nom du fichier transmis et à vérifier si un tel fichier existe :

        // Décode le nom de fichier récupéré, susceptible de contenir des espaces et autres caractères spéciaux,
        // et prépare l'objet File       enc: StandardCharsets.UTF_8.toString()  donnerait "UTF-8"
        fichierRequis = URLDecoder.decode(fichierRequis, "UTF-8");
        File fichier = new File(chemin, fichierRequis);

        // Vérifie que le fichier existe bien
        if (!fichier.exists()) {
            // Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //--------------------------------------------------------------------------------------------------------------
        // Une fois ces contrôles réalisés, il nous faut encore récupérer le type du fichier transmis, à l'aide de la
        // méthode getMimeType() de l'objet ServletContext. Sa documentation nous indique qu'elle retourne le type du
        // contenu d'un fichier en prenant pour argument son nom uniquement. Si le type de contenu est inconnu, alors
        // la méthode renvoie null :

        // Récupère le type du fichier
        String type = getServletContext().getMimeType(fichier.getName());

        // Si le type de fichier est inconnu, alors on initialise un type par défaut
        if (type == null) {
            type = "application/octet-stream";
        }
        //--------------------------------------------------------------------------------------------------------------
        //                               Génération de la réponse HTTP
        //--------------------------------------------------------------------------------------------------------------
        // Après tous ces petits traitements, nous avons maintenant tout en main pour initialiser une réponse HTTP et
        // y renseigner les en-têtes nécessaires, à savoir :
        // Voici donc le code en charge de l'initialisation de la réponse :

        // Initialise la réponse HTTP
        response.reset();
        response.setBufferSize(TAILLE_TAMPON);// private static final int DEFAULT_BUFFER_SIZE = 10240; // 10 ko
        response.setContentType(type);
        response.setHeader("Content-Length", String.valueOf(fichier.length()));//
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fichier.getName() + "\"");
        //response.setHeader( "Content-Disposition", "attachment;filename=" + filename );
        /*
        Voici quelques explications sur l'enchaînement ici réalisé :
        ***********************************************************
        * reset() : efface littéralement l'intégralité du contenu de la réponse initiée par le conteneur ;
        * setBufferSize() : méthode à appeler impérativement après un reset() ;
        * setContentType() : spécifie le type des données contenues dans la réponse ;
        * nous retrouvons ensuite les deux en-têtes HTTP, qu'il faut construire "à la main" via des appels à setHeader()
        */

        //--------------------------------------------------------------------------------------------------------------
        //                                    Lecture et envoi du fichier
        //--------------------------------------------------------------------------------------------------------------
        // Nous arrivons enfin à la dernière étape du processus : la lecture du flux et l'envoi au client !
        // Commençons par mettre en place proprement l'ouverture des flux :

        // Prépare les flux
        BufferedInputStream entree = null;
        BufferedOutputStream sortie = null;
        try {
            // Ouvre les flux
            entree = new BufferedInputStream(new FileInputStream(fichier), TAILLE_TAMPON);
            sortie = new BufferedOutputStream(response.getOutputStream(), TAILLE_TAMPON);
            //renvoie une reponse http avec le fichier à en question à télécharger

            // Lit le fichier et écrit son contenu dans la réponse HTTP
            byte[] tampon = new byte[TAILLE_TAMPON];
            int longueur;

            while ((longueur = entree.read(tampon)) > 0) {// !=-1
                sortie.write(tampon, 0, longueur);//flux de téléchargement
            }
        } finally {
            sortie.close();
            entree.close();
        }
    }
}