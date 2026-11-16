# HerDrive 🚗💗

> Aplicativo Android de apoio à autonomia automotiva, com cadastro de veículos e consulta à Tabela FIPE. Desenvolvido para a disciplina de **Programação para Dispositivos Móveis (PDM)**.

O HerDrive ajuda a organizar informações básicas dos veículos e consultar preços médios de referência de forma acessível. Esta entrega é uma **versão acadêmica reduzida** da proposta do projeto, concentrada nos requisitos de interface, navegação, CRUD em nuvem e consumo de API REST.

## 🛠️ Tecnologias

- Kotlin 🔷
- Android Studio e Gradle 🛠️
- Jetpack Compose + Material Design 3 🎨
- Navigation Compose 🧭
- Firebase Cloud Firestore ☁️
- Corrotinas Kotlin (`suspend`, `launch`, `await`) ⚡
- Retrofit 2 + Gson Converter 🌐
- API pública FIPE (Parallelum) 🚘

## ✨ Funcionalidades

- **Início:** apresentação do HerDrive e acesso rápido às demais telas.
- **Meus carros:** cadastro, listagem, edição e exclusão de veículos, com busca local por marca, modelo ou ano.
- **Tabela FIPE:** seleção de marca, modelo e ano para consultar preço médio de referência, combustível e informações do veículo.
- **Navegação:** barra inferior entre as três telas.
- **Feedback:** indicação de carregamento e mensagens de erro para operações de rede.

> **Escopo:** esta versão não inclui login, assistente de IA, agendamentos, oficinas ou pagamentos. O valor FIPE é uma **referência de mercado**, não uma avaliação individual nem uma oferta de compra ou venda.

## 🚀 Como utilizar

1. Clone o repositório ou baixe o projeto em ZIP.
2. Abra a pasta **HerDrive** no **Android Studio** (`File > Open`). Aguarde o reconhecimento do projeto Gradle.
3. Tenha instalado o **Android SDK 35** e um **JDK 17 completo**. Se houver erro de Java/Gradle, confira o **Gradle JDK** ou os **Daemon JVM Criteria** nas configurações do Android Studio e selecione Java 17.
4. Acesse o [Firebase Console](https://console.firebase.google.com/), crie ou selecione o projeto do HerDrive e registre um aplicativo **Android** com o nome de pacote:

   ```text
   br.com.herdrive
   ```

5. Baixe o arquivo `google-services.json` correspondente **a esse aplicativo Firebase** e coloque-o em:

   ```text
   HerDrive/app/google-services.json
   ```

6. No Firebase Console, abra **Firestore Database** e crie um banco **Cloud Firestore** (banco padrão, `(default)`). A aplicação utiliza a coleção `veiculos`, que pode ser criada automaticamente no primeiro cadastro.
7. Configure as **regras de segurança do Firestore** de acordo com o ambiente. Esta entrega não possui autenticação; regras restritivas de produção podem bloquear a leitura e a gravação. Para a avaliação acadêmica, use apenas **dados fictícios** e permissões de teste **temporárias**, com prazo curto. **Não publique um banco com leitura/escrita aberta.**
8. No Android Studio, clique em **Sync Project with Gradle Files** e execute o aplicativo em um emulador ou dispositivo Android conectado à internet.

> **Atenção:** o arquivo `google-services.json` deve ser obtido do projeto Firebase autorizado. Ele está incluído no `.gitignore` e **não deve ser enviado a este repositório**. Também não publique chaves administrativas, credenciais de serviço ou arquivos de assinatura do aplicativo. Proteger o arquivo de configuração não substitui as **regras de segurança** do Firestore.

## 🚘 Como testar o aplicativo

1. Abra **Meus carros** e cadastre um veículo informando **marca**, **modelo** e **ano**.
2. Confira se o cadastro aparece no aplicativo e na coleção `veiculos` do Firestore (**Create / Read**).
3. Pesquise pela marca, pelo modelo ou pelo ano (**filtro local**, aplicado sobre os veículos carregados).
4. Edite um cadastro e verifique a alteração no Firestore (**Update**).
5. Exclua um cadastro e confira sua remoção (**Delete**).
6. Abra **Tabela FIPE**, selecione marca, modelo e ano e consulte as informações. Essa etapa usa **Retrofit e a API pública**, não o Firestore.

## ☁️ Estrutura dos dados no Firebase

Coleção: `veiculos`

```text
veiculos/
└── <id_gerado_pelo_firestore>/
    ├── marca: "Toyota"     # string
    ├── modelo: "Corolla"   # string
    └── ano: "2020"        # string
```

O identificador é gerado automaticamente pelo Firestore. Os campos `marca`, `modelo` e `ano` são armazenados como **texto**. O arquivo `VeiculosRepositorio.kt` centraliza as operações `add()`, `get()`, `update()` e `delete()`, utilizando `await()` para aguardar as respostas sem bloquear a interface.

## 🌐 Integração com a API FIPE

A API é utilizada **somente para consulta** e não exige configuração no Firebase.

- **Endereço base:** `https://fipe.parallelum.com.br/api/v2/`
- **Marcas:** `GET cars/brands`
- **Modelos:** `GET cars/brands/{marca}/models`
- **Anos:** `GET cars/brands/{marca}/models/{modelo}/years`
- **Preço:** `GET cars/brands/{marca}/models/{modelo}/years/{ano}`

A interface `FipeApi.kt` define as requisições com `@GET` e `@Path`. O `FipeClient` configura o Retrofit e o `GsonConverterFactory` para transformar respostas JSON em objetos Kotlin. A tela `ConsultaFipeScreen.kt` usa **corrotinas** e **estado do Compose** para buscar os dados e atualizar a interface.

> Consultas à FIPE dependem de conexão com a internet e da disponibilidade e dos limites de uso do serviço externo.

## 📁 Estrutura de diretórios

```text
HerDrive/
├── app/
│   ├── google-services.json             # Firebase local; NÃO versionar
│   ├── build.gradle.kts                  # Dependências e configuração do Android
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml       # Permissão INTERNET e Activity
│           ├── java/br/com/herdrive/
│           │   ├── MainActivity.kt       # Scaffold e navegação
│           │   ├── data/
│           │   │   ├── FipeApi.kt        # Retrofit, endpoints e modelos da FIPE
│           │   │   ├── Veiculo.kt        # Modelo de veículo
│           │   │   └── VeiculosRepositorio.kt  # CRUD do Firestore
│           │   └── ui/
│           │       ├── ConsultaFipeScreen.kt  # Tela da Tabela FIPE
│           │       ├── HerDriveTheme.kt       # Paleta e tema Compose
│           │       ├── InicioScreen.kt        # Tela inicial
│           │       └── VeiculosScreen.kt      # Cadastro, busca e listagem
│           └── res/
│               ├── drawable/
│               │   └── ic_herdrive.xml
│               └── values/
│                   └── themes.xml        # Ou styles.xml; sem tema duplicado
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar            # Se gerado/incluído no projeto
│       └── gradle-wrapper.properties
├── .gitignore
├── README.md
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── gradlew                           # Se gerado/incluído no projeto
└── gradlew.bat                       # Se gerado/incluído no projeto
```

> **Observação:** mantenha **uma única definição** do recurso `Theme.HerDrive`. Se `styles.xml` e `themes.xml` definirem o mesmo tema, o Android apresentará erro de recurso duplicado. Arquivos antigos de consulta de CEP (`ViaCepApi.kt` e `ConsultaCepScreen.kt`) não são utilizados nesta versão.

## 🔒 Segurança e versionamento

- O `.gitignore` exclui configurações locais, saídas de compilação, arquivos de assinatura, credenciais e `google-services.json`.
- **Não** adicione chaves privadas, contas de serviço Firebase, senhas, arquivos `.env` ou certificados ao Git.
- O arquivo `gradle.properties` do projeto pode ser versionado **desde que não contenha senhas ou tokens**.
- O SDK Android do Firebase **não usa chave administrativa** para ler e gravar veículos; o acesso é controlado pelas regras do Firestore.
- Se alguma credencial já tiver sido enviada ao Git, acrescentar seu nome ao `.gitignore` **não a remove do histórico**: é necessário revogar/substituir a credencial e tratar o histórico do repositório.

## 📚 Referências

- [Android Developers — Jetpack Compose](https://developer.android.com/develop/ui/compose)
- [Android Developers — Navigation Compose](https://developer.android.com/develop/ui/compose/navigation)
- [Firebase — Cloud Firestore](https://firebase.google.com/docs/firestore)
- [Retrofit](https://square.github.io/retrofit/)
- [FIPE API](https://fipe.parallelum.com.br/)

---

**HerDrive** — mais autonomia para entender e cuidar do seu veículo. 💗
