FROM redis:7.2.5-alpine3.20

EXPOSE 6379

CMD ["redis-server"]

########## [NOTICE] ##########
# docker image build
#docker build -f Dockerfile . -t simple-redis

# docker container run
#docker run -d -p 6379:6379 simple-redis --name simple-redis