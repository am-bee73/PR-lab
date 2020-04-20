
#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>
#include <stdint.h>
#include <errno.h>
#include <stdbool.h>
#include <arpa/inet.h>
#define ACCESS_VIOLATION -1
#define FILE_NOT_FOUND -2
#define ERROR(s) {/*fprintf(stderr,"%d-",errno);*perror(s);*/ return(-1);}

//realizam structura petnru a defini parametrii corpului cererii HTTP
typedef struct
{
	char version[10];
	int status;
	char status_message[15];
	char content_type[50];
	int content_length;
	char *body;
} HTTP_FORM;

HTTP_FORM retval;


/* Citeste cererea clientului. Pune numele fisierului intr-un string.
* Daca sintaxa nu este corecta sau lipsesc retururi la linii
* se trimite -1. In caz contrar functia transmite 0.
* requestFromClient este sirul de 1000 octeti care contine cererea provenita de la client.
* requestSize trebuie sa egala cu 1000 (si nu la lungimea sirului de caractere).
*/

int parseRequest(char* requestFromClient, int requestSize, char* string, int stringSize)
{

	char *charPtr[4], *end;
	requestFromClient[requestSize-1]='\0';
				//Permite utilizarea strchr() - atentie, nu merge daca requestSize indica lungimea sirului de caractere
	if( (end=strstr(requestFromClient,"\r\n"))==NULL) return(-1);


	// Verificarea syntaxei (GET fisier HTTP/1.1)
	charPtr[0]=requestFromClient;	//Inceputul cererii (GET in principiu)
	//Se cauta primul spatiu, codul ascii 0x20 (in hexa), este inceputul numelui fisierului
	charPtr[1]=strchr(requestFromClient,' ');
	if(charPtr[1]==NULL) return(-1);
	charPtr[2]=strchr(charPtr[1]+1,' ');
	if(charPtr[2]==NULL) return(-1);
	charPtr[3]=strchr(charPtr[2]+1,'\r');
	if(charPtr[3]==NULL) return(-1);

	//Se separa sirurile
	*charPtr[1]='\0';
	*charPtr[2]='\0';
	*charPtr[3]='\0';
// GET /index.html HTTP/1.1
//host:127.0.0.1:4000
	if(strcmp(charPtr[0],"GET")!=0) return(-1);
	if(strcmp(charPtr[2]+1,"HTTP/1.1")!=0) return(-1);
	strncpy(string,charPtr[1]+2,stringSize);//Se decaleasa sirul cu 2 octeti: primul octet este '\0', al doilea decalaj permite sa retragem "/"


	if(string[stringSize-1]!='\0'){
		fprintf(stderr,"\nErreur parseRequest(): lungimea sirului string nu este suficienta (stringSize=%d)\n",stringSize);
		exit(3);
	}

	if( *(charPtr[1]+2) == '\0') fprintf(stderr,"\nDEBUG-SERVEUR: nomele fisieruui est vid -\nDEBUG-SERVEUR: - se asociaza fisierul implicit index.html\n");
	else fprintf(stderr,"\nDEBUG-SERVEUR: numele fisierului cerut este %s\n",string);

	if( *(charPtr[1]+2) == '\0') strcpy(string,"index.html");

	return(0);
	*(end+4)='\0';
}
//functie pentru verificarea corecteitudinii imaginii
bool image(char *file)
{
	char punct[] = ".";
	char *exten = strtok(file, punct);
	exten= strtok(NULL, punct);

	int compareFlag = strcmp(exten, "jpg");
	if (!compareFlag)
	{
		return true;
	}
	return false;
}

//in aceasta functie ne retureaza statusul despre pagina accesata
// aici predifinim erorile care pot avea loc in timpul rularii comenzii GET.
void request_Status(int client_socket_descriptor)
{
	strcpy(retval.version, "HTTP/1.1");
	strcpy(retval.content_type, "text/html;charset=UTF-8");

	char request[2000] = {0};
	if(read(client_socket_descriptor, request, sizeof(request)) < 0)
	ERROR("Eroor on read");

	char *string;
	string = calloc(50, sizeof(char));
	char *content=NULL;
	char *filename=NULL;
	int fileLength;

// Eroare la accesarea , introducerea gresita a parametrilor
	int flag = parseRequest(request, sizeof(request), string, 50);
	if (flag == -1)
	{
		filecontent("siteWeb\\file400.html", &content, &fileLength);
		retval.status = 400;
		strcpy(retval.status_message, "BAD REQUEST");
		retval.content_length = fileLength;
		retval.body = calloc(fileLength+1, sizeof(char));
		strcpy(retval.body, content);
	}
	else
	{
		filename = calloc(50, sizeof(char));
		strcpy(filename, "siteWeb\\");
		strcat(filename, string);
		int fileFlag = filecontent(filename, &content, &fileLength);
//eroarea din partea serverului
		if (fileFlag == ACCESS_VIOLATION)
		{
			filecontent("siteWeb\\file500.html", &content, &fileLength);
			retval.status = 500;
			strcpy(retval.status_message, "INTERNAL SERVER ERROR");
			retval.content_length = fileLength;
			retval.body = calloc(fileLength+1, sizeof(char));
			strcpy(retval.body, content);
		}
		else if (fileFlag == FILE_NOT_FOUND)// eroare in caz de fisierul accesat nu a fost gasit
		{
			filecontent("siteWeb\\file404.html", &content, &fileLength);
			retval.status = 404;
			strcpy(retval.status_message, "file NOT FOUND");
			retval.content_length = fileLength;
			retval.body = calloc(fileLength+1, sizeof(char));
			strcpy(retval.body, content);
		}
		else
		{//afisarea imaginii
			if (image(string))
			{
				strcpy(retval.content_type, "image/jpg");
			}
			else
			{
				strcpy(retval.content_type, "text/html;charset=UTF-8");
			}
			//returnam statusul bun de afisare a comenzii cerute
			retval.status = 200;
			strcpy(retval.status_message, "OK");
			retval.content_length = fileLength;
			retval.body = calloc(fileLength+1, sizeof(char));
			memcpy(retval.body, content, fileLength);
		}
		free(filename);
		free(content);
	}

	free(string);
	puts("Trimitem raspunsul cerut:");
	send_response(client_socket_descriptor, fileLength);
	free(retval.body);
}
//functie pentru deschiderea imaginii
int filecontent(char *filename, char **content, int *body_length)
{
	FILE *fp = fopen(filename, "rb");
	if (fp != NULL)
	{
		fseek(fp, 0L, SEEK_END);
		long fileSize = ftell(fp);
		*content = calloc(fileSize+1, sizeof(char));
		fseek(fp, 0L, SEEK_SET);
		int length = fread(*content, sizeof(char), fileSize, fp);
		*body_length = fileSize;
		fclose(fp);
	}
	else
	{
		if (errno == ENOENT)
		{
			return -2;
		}
		else if (errno == EACCES)
		{
			return -1;
		}
	}
	return 0;
}
// trimitem raspunsul la client
void send_response(int client_socket_descriptor, int *body_length)
{
	char *res = calloc(sizeof(retval) + body_length + 1, sizeof(char));
	create_response_string(&res, retval, body_length);
	int flag;
	if ((flag = write(client_socket_descriptor, res, sizeof(HTTP_FORM) + body_length)) < 0 )
	ERROR("Could not send retval");

	free(res);
}
//crearea raspunsului pentru client
void create_response_string(char **res, HTTP_FORM retval, int *body_length)
{
	int end = sprintf(*res, "%s %d %s\r\nContent-Type: %s\r\nContent_Length: %d\r\n\r\n", retval.version, retval.status, retval.status_message, retval.content_type, body_length);
	memcpy(*res + end, retval.body, body_length);
}

int main(int argc, char const *argv[])
{
	setbuf(stdout, NULL);
	int server_socket_descriptor;
	int client_socket_descriptor;
	long valRead;
	struct sockaddr_in server_address;
	struct sockaddr_in client_address;
	int addrlen = sizeof(server_address);
  int pid;

	if (argc != 2) {
	fprintf (stdout, "usage: %s hostname\n", argv[0]);
	exit (-1);
}

server_address.sin_family= AF_INET; // addres family domeniu de comunicare actual - AF_INET pentru protocolul ipv4
server_address.sin_addr.s_addr = INADDR_ANY; // internet address
server_address.sin_port= htons((u_short) atoi(argv[1])); //port nummber

//Crearea unui nou socket prin apelul functiei socket().
if ((server_socket_descriptor = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0)
	ERROR ("socket");

	memset(server_address.sin_zero, '\0', sizeof(server_address.sin_zero));

	if (bind(server_socket_descriptor, (struct sockaddr *)&server_address, sizeof(server_address)) < 0)
   ERROR ("bind");

	listen(server_socket_descriptor,5);

	while(1)
	{
		printf("\nConectarea este reusita , Serverul in asteptare de comenzi.\n\n");
		if ((client_socket_descriptor = accept(server_socket_descriptor, (struct sockaddr *)&client_address, &addrlen)) < 0)
		ERROR (" ");

		 pid = fork();
		 if (pid<0){
		 		ERROR("ERROR on FORK"); }

		else if (pid==0){

		 printf("Adresa IP a clientului:%s cu portul: %d\n", inet_ntoa(client_address.sin_addr),(int) ntohs(client_address.sin_port));
		 close(server_socket_descriptor);
		request_Status(client_socket_descriptor);
		}
		else if (pid>0){
			close(client_socket_descriptor);
		}
	}
	return 0;
}
