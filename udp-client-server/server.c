#include<stdio.h>
#include<netinet/in.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netdb.h>
#include<string.h>
#include<stdlib.h>
#define BUFSIZE 1024
#define PORT 43454
#define SA struct sockaddr

void func(int sockfd)
{
	char buff[BUFSIZE];
	int n,clen;
	struct sockaddr_in cli;
	clen=sizeof(cli);
	
	for(;;)
	{
		bzero(buff,BUFSIZE);
		//primeste cererea de la client
		recvfrom(sockfd,buff,sizeof(buff),0,(SA *)&cli,&clen);
		printf("From client %s\n To client",buff);
		bzero(buff,BUFSIZE);
		n=0;
		while((buff[n++]=getchar())!='\n');
		//trimite raspunsul catre client
		sendto(sockfd,buff,sizeof(buff),0,(SA *)&cli,clen);
		
		if(strncmp("exit",buff,4)==0)
		{
			printf("Server Exit...\n");
			break;
		}
	}
}

int main()
{
	int sockfd;
	struct sockaddr_in servaddr;
	//crearea socketului udp
	sockfd=socket(AF_INET,SOCK_DGRAM,0);
	
	if(sockfd==-1)
	{
		printf("socket creation failed...\n");
		exit(0);
	}
	else
	printf("Socket successfully created..\n");
	bzero(&servaddr,sizeof(servaddr)); 
	servaddr.sin_family=AF_INET; // addres family domeniu de comunicare actual - AF_INET pentru protocolul ipv4
	servaddr.sin_addr.s_addr=htonl(INADDR_ANY); // internet address
	servaddr.sin_port=htons(PORT);// port nummber
	 //Legarea (bind) socket-ului creat la o adresă şi la un port printr-un apel bin
	if((bind(sockfd,(SA *)&servaddr,sizeof(servaddr)))!=0)
	{
		printf("socket bind failed...\n");
		exit(0);
	}
	else
	printf("Socket successfully binded..\n");
	func(sockfd);
	close(sockfd);
}
