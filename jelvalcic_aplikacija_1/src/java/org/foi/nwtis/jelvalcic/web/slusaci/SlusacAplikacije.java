/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jelvalcic.web.slusaci;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.jelvalcic.aplikacija_1.ObradaMeteoPodataka;
import org.foi.nwtis.jelvalcic.aplikacija_1.Posrednik;
import org.foi.nwtis.jelvalcic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.jelvalcic.web.kontrole.KorisnickaKonfiguracija;

/**
 * Web application lifecycle listener.
 *
 * @author jelvalcic
 * Klasa slušača aplikacije
 */
public class SlusacAplikacije implements ServletContextListener {
    //Dretva ObradaPoruke
    private ObradaMeteoPodataka obradaMeteoPodataka = null;
    private Posrednik posrednik;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        javax.servlet.ServletContext context = sce.getServletContext();
        String path = context.getRealPath("WEB-INF") + java.io.File.separator;
        String datoteka = path + sce.getServletContext().getInitParameter("konfiguracija");
        KontekstAplikacije.setKontekst(sce.getServletContext()); 
        System.out.println(datoteka);
        
        KorisnickaKonfiguracija bpKonf = new KorisnickaKonfiguracija(datoteka);
        BP_Konfiguracija bpKonfig = new BP_Konfiguracija(datoteka);
        
        sce.getServletContext().setAttribute("BP_Konfiguracija", bpKonf);
        sce.getServletContext().setAttribute("BP_Konfig", bpKonfig);

        System.out.println("Konfiguracija je ucitana.");
        System.out.println(bpKonf.getBpKonfig().getDriver_database());

        obradaMeteoPodataka = new ObradaMeteoPodataka();
        obradaMeteoPodataka.setBpKonf(bpKonf);
        obradaMeteoPodataka.setContext(sce.getServletContext());
        obradaMeteoPodataka.setName("ObradaMeteoPodataka");
        obradaMeteoPodataka.start();
        
        int port = Integer.parseInt(bpKonf.getKonfiguracija().dajPostavku("port"));
        posrednik = new Posrednik(port,bpKonf);
        posrednik.setName("Posrednik");
        posrednik.start();
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        if (obradaMeteoPodataka != null || obradaMeteoPodataka.isAlive() || obradaMeteoPodataka.isDaemon()) {
            //prekida se rad dretve ukoliko je došlo do bilo kakvog prekida
            //osigurava se da ne ostane raditi negdje u pozadini
            obradaMeteoPodataka.interrupt();
        }
         if (posrednik != null || posrednik.isAlive() || posrednik.isDaemon()) {
           
        posrednik.interrupt();
         }
        System.out.println("Aplikacija zaustavljena.");
    }
}
