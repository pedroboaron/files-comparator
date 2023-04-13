package com.boti.filescomparator.service;

import com.boti.filescomparator.dto.ItemComparacao;
import com.boti.filescomparator.feign.client.MicrosoftClient;
import com.boti.filescomparator.feign.interfaces.SimpleAuthProvider;
import com.microsoft.aad.msal4j.*;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@RequiredArgsConstructor
@Service
public class CloudService {

    private final ResourceLoader resourceLoader;

    private final MicrosoftClient microsoftClient;
    private final static String CLIENT_ID = "3dc4c657-98be-4eb3-8d96-e8138e0f3faa";
    private final static String USER_ID = "53b3f9ef-d92b-4ddb-b051-24e5acad1037";
    private final static String AUTHORITY = "https://login.microsoftonline.com/d22ddcb7-943f-4c7b-a7a2-96eb07721d65/";
    private final static String CLIENT_SECRET = "mp38Q~1H_mFW5DiaqOGLJt2lgsV.8wr1JqDHRbDV";
    private final static Set<String> SCOPE = Collections.singleton(".default");

    public String login() throws Exception {
        return acquireToken().accessToken();
    }

    public List getUsers() throws Exception {
        GraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(new SimpleAuthProvider(acquireToken().accessToken())).buildClient();

        UserCollectionPage children = graphClient.users()
                .buildRequest()
                .get();

        return children.getCurrentPage();
    }

    public List<ItemComparacao> getFile() throws Exception {
        GraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(new SimpleAuthProvider(acquireToken().accessToken())).buildClient();

//        String stream = microsoftClient.getFile(acquireToken().accessToken());
        String das = getPdfFileByItemIdToString("01WDX6HQH3SVA665HQZNA2EZGXRYRDFMSK");
        String extrato = getPdfFileByItemIdToString("01WDX6HQH3SVA665HQZNA2EZGXRYRDFMSK");
        return compareSimplesNacional(das,extrato);
    }

    private List<ItemComparacao> compareSimplesNacional(String das, String extrato){
        List<ItemComparacao> resultado = new ArrayList<>();
        resultado.add(new ItemComparacao("Periodo Apuracao",getPeridoApuracaoDas(das).equalsIgnoreCase(getPeridoApuracaoDas(extrato)),""));
        resultado.add(new ItemComparacao("CNPJ",getCNPJDas(das).equalsIgnoreCase(getCNPJDas(extrato)),""));
        resultado.add(new ItemComparacao("Valor",getValorDas(das).equalsIgnoreCase(getValorDas(extrato)),""));
        return resultado;
    }

    private String getPeridoApuracaoDas(String text){
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha:linhas
             ) {
            if(linha.startsWith("Período de Apuração:")){
                return linha.substring(20);
            }
        }
        return null;
    }

    private String getCNPJDas(String text){
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha:linhas
        ) {
            if(linha.startsWith("CNPJ Matriz:")){
                return linha.substring(12);
            }
        }
        return null;
    }

    private String getValorDas(String text){
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha:linhas
        ) {
            if(linha.startsWith("Período de Apuração:")){
                return linha.substring(20);
            }
        }
        return null;
    }
//    private String getPeridoApuracaoExtrato(String text){}
public String getPdfFileByItemIdToString(String itemId) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://graph.microsoft.com/v1.0/users/"+USER_ID+"/drive/items/"+itemId+"/content")
                .method("GET",null)
                .addHeader("Authorization", "Bearer "+ acquireToken().accessToken())
                .build();
        Response response = client.newCall(request).execute();
        //nome do arquivo
        String[]content = response.headers().get("content-disposition").split(";");
        String fileName = Arrays.stream(content).filter(c -> c.startsWith("filename=")).findFirst().orElse("semNome");
        fileName = fileName.substring(10, fileName.length()-1);

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(".").getFile() + "/" + fileName);
        saveFile(response.body().byteStream(),file);
        //lendo o arquivo
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

    private void saveFile(InputStream stream, File file){
        FileOutputStream fop = null;

        try {
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = stream.readAllBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static IAuthenticationResult acquireToken() throws Exception {

        // This is the secret that is created in the Azure portal when registering the application
        IClientCredential credential = ClientCredentialFactory.createFromSecret(CLIENT_SECRET);
        ConfidentialClientApplication cca =
                ConfidentialClientApplication
                        .builder(CLIENT_ID, credential)
                        .authority(AUTHORITY)
                        .build();

        // Client credential requests will by default try to look for a valid token in the
        // in-memory token cache. If found, it will return this token. If a token is not found, or the
        // token is not valid, it will fall back to acquiring a token from the AAD service. Although
        // not recommended unless there is a reason for doing so, you can skip the cache lookup
        // by using .skipCache(true) in ClientCredentialParameters.
        ClientCredentialParameters parameters =
                ClientCredentialParameters
                        .builder(SCOPE)
                        .build();

        return cca.acquireToken(parameters).join();
    }
}
