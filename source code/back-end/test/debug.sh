rm -f ./main
go mod download
go build -gcflags "all=-N -l" main.go
dlv --listen=:2345 --headless=true --api-version=2 --accept-multiclient exec ./main
