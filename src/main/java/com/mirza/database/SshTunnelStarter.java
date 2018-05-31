package com.mirza.database;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

/**
 * Created by yach0217 on 29.05.2018.
 */

@Service
public class SshTunnelStarter {

    private String strSshUser = "root";
    private String strSshPassword = "june1994";
    private String strSshHost = "167.99.136.105";
    private int nSshPort = 22;
    private String strRemoteHost = "167.99.136.105";
    private int nLocalPort = 3366;
    private int nRemotePort = 5434;

    private Session session;

    @PostConstruct
    public void init() throws Exception {
        final JSch jsch = new JSch();
        session = jsch.getSession(strSshUser, strSshHost, nSshPort);
        session.setPassword(strSshPassword);

        final Properties config = new Properties();
        Class.forName("org.postgresql.Driver");
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
    }

    @PreDestroy
    public void shutdown() throws Exception {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}