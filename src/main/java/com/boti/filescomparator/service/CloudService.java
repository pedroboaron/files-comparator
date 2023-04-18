package com.boti.filescomparator.service;

import com.boti.filescomparator.dto.ItemComparacao;
import com.boti.filescomparator.dto.cadastroEmpresa.EmpresaDtoResponse;
import com.boti.filescomparator.feign.client.CadastroEmpresaClient;
import com.boti.filescomparator.feign.interfaces.SimpleAuthProvider;
import com.microsoft.aad.msal4j.*;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.*;
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
import java.time.LocalDate;
import java.util.*;


@RequiredArgsConstructor
@Service
public class CloudService {

    private final ResourceLoader resourceLoader;

    private final CadastroEmpresaClient cadastroEmpresaClient;
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

    private List<DriveItem> getMonthDriveChildren(GraphServiceClient graphClient,DriveItemCollectionPage root, Integer empresaId, LocalDate periodo){

        DriveItem depFiscal = new DriveItem();
        DriveItem empresas = new DriveItem();
        DriveItem empresa = new DriveItem();
        DriveItem movFiscal = new DriveItem();
        DriveItem ano = new DriveItem();
        DriveItem mes = new DriveItem();
        for (DriveItem drive : root.getCurrentPage()) {
            if (drive.name.equalsIgnoreCase("DEPARTAMENTO FISCAL")) {
                depFiscal = drive;
                break;
            }
        }
        for (DriveItem drive: graphClient.users(USER_ID).drives(root.getCurrentPage().get(0).parentReference.driveId).items(depFiscal.id).children().buildRequest().get().getCurrentPage()) {
            if (drive.name.equalsIgnoreCase("EMPRESAS")) {
                empresas = drive;
                break;
            }
        }
        for (DriveItem drive: graphClient.users(USER_ID).drives(root.getCurrentPage().get(0).parentReference.driveId).items(empresas.id).children().buildRequest().get().getCurrentPage()) {
            if (drive.name.startsWith(empresaId +"-")) {
                empresa = drive;
                break;
            }
        }
        for (DriveItem drive: graphClient.users(USER_ID).drives(root.getCurrentPage().get(0).parentReference.driveId).items(empresa.id).children().buildRequest().get().getCurrentPage()) {
            if (drive.name.equalsIgnoreCase("MOVIMENTO FISCAL")) {
                movFiscal = drive;
                break;
            }
        }
        for (DriveItem drive: graphClient.users(USER_ID).drives(root.getCurrentPage().get(0).parentReference.driveId).items(movFiscal.id).children().buildRequest().get().getCurrentPage()) {
            if (drive.name.equalsIgnoreCase(String.valueOf(periodo.getYear()))) {
                ano = drive;
                break;
            }
        }
        for (DriveItem drive: graphClient.users(USER_ID).drives(root.getCurrentPage().get(0).parentReference.driveId).items(ano.id).children().buildRequest().get().getCurrentPage()) {
            if (drive.name.replace("0","").equalsIgnoreCase(String.valueOf(periodo.getMonth().getValue()))) {
                mes = drive;
            }
        }
        return graphClient.users(USER_ID).drives(root.getCurrentPage().get(0).parentReference.driveId).items(mes.id).children().buildRequest().get().getCurrentPage();
    }
    public List<ItemComparacao> compare(Integer empresaId, LocalDate periodo) throws Exception {
        EmpresaDtoResponse res = cadastroEmpresaClient.getEmpresaById(empresaId);
        // a partir daqui adicionar switch para cada tipo de empresa ter sua validação
        GraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(new SimpleAuthProvider(acquireToken().accessToken())).buildClient();
        DriveItemCollectionPage root = graphClient.users(USER_ID).drive().root().children()
                .buildRequest()
                .get();
        List<DriveItem> drivesOfMonth = getMonthDriveChildren(graphClient,root,res.getCodigo(),periodo);

//

        return compareSimplesNacionalIss(drivesOfMonth, res);
    }


    private List<ItemComparacao> compareSimplesNacionalIss(List<DriveItem> drivesOfMonth, EmpresaDtoResponse res) throws Exception {
        DriveItem pgdasDrive = new DriveItem();
        DriveItem dasDrive = new DriveItem();
        DriveItem relatorioDrive = new DriveItem();
        String pgdas;
        String das;
        String relatorio;

        for (DriveItem drive: drivesOfMonth) {
            if (drive.name.startsWith(res.getCodigo() +"-PGDAS RETIF")) {
                pgdasDrive = drive;
            }
            if (drive.name.startsWith(res.getCodigo() +"-DAS RETIF")) {
                dasDrive = drive;
            }
            if (drive.name.startsWith(res.getCodigo() +"-Serviços RETIF")) {
                relatorioDrive = drive;
            }
        }
        pgdas = getPdfFileByItemIdToString(pgdasDrive.id);
        das = getPdfFileByItemIdToString(dasDrive.id);
        relatorio = getPdfFileByItemIdToString(relatorioDrive.id);

        List<ItemComparacao> resultado = new ArrayList<>();
        resultado.add(validaValor(pgdas,das,relatorio));
        resultado.add(validaCnpj(pgdas,das,relatorio));
        resultado.add(validaPeriodo(pgdas,das,relatorio));
        return resultado;
    }

    private ItemComparacao validaValor(String pgdas, String das, String relatorio){
        ItemComparacao itemComparacao = new ItemComparacao("Valor");
        itemComparacao.setDesc("teste descrição");
        itemComparacao.setStatusAprovado(true);
        return itemComparacao;
    }
    private ItemComparacao validaPeriodo(String pgdas, String das, String relatorio){
        ItemComparacao itemComparacao = new ItemComparacao("Periodo");
        itemComparacao.setDesc("teste descrição");
        itemComparacao.setStatusAprovado(true);
        return itemComparacao;
    }
    private ItemComparacao validaCnpj(String pgdas, String das, String relatorio){
        ItemComparacao itemComparacao = new ItemComparacao("Cnpj");
        itemComparacao.setDesc("teste descrição");
        itemComparacao.setStatusAprovado(false);
        return itemComparacao;
    }
    private String getPeridoApuracaoDas(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("Período de Apuração:")) {
                return linha.substring(20);
            }
        }
        return null;
    }

    private String getCNPJDas(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("CNPJ Matriz:")) {
                return linha.substring(12);
            }
        }
        return null;
    }

    private String getValorDas(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("Período de Apuração:")) {
                return linha.substring(20);
            }
        }
        return null;
    }


    private String getPeridoApuracaoPgdas(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("Período de Apuração:")) {
                return linha.substring(20);
            }
        }
        return null;
    }

    private String getCNPJPgdas(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("CNPJ Matriz:")) {
                return linha.substring(12);
            }
        }
        return null;
    }

    private String getValorPgdas(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("Período de Apuração:")) {
                return linha.substring(20);
            }
        }
        return null;
    }

    private String getPeridoApuracaoRelatorio(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("Período de Apuração:")) {
                return linha.substring(20);
            }
        }
        return null;
    }

    private String getCNPJRelatorio(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("CNPJ Matriz:")) {
                return linha.substring(12);
            }
        }
        return null;
    }

    private String getValorRelatorio(String text) {
        String[] linhas = text.toString().split("\\r?\\n");
        for (String linha : linhas
        ) {
            if (linha.startsWith("Período de Apuração:")) {
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
                .url("https://graph.microsoft.com/v1.0/users/" + USER_ID + "/drive/items/" + itemId + "/content")
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + acquireToken().accessToken())
                .build();
        Response response = client.newCall(request).execute();
        //nome do arquivo
        String[] content = response.headers().get("content-disposition").split(";");
        String fileName = Arrays.stream(content).filter(c -> c.startsWith("filename=")).findFirst().orElse("semNome");
        fileName = fileName.substring(10, fileName.length() - 1);

        File file = File.createTempFile(fileName,".pdf");
        saveFile(response.body().byteStream(), file);
        //lendo o arquivo
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        file.deleteOnExit();
        return text;
    }

    private void saveFile(InputStream stream, File file) {
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
