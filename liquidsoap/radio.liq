settings.log.stdout.set(true)
settings.server.socket := true
settings.server.socket.path := "/run/liquidsoap/control.sock"

def restart_streamora(_) =
  restart()
  "Restarting"
end

server.register(
  namespace="streamora",
  description="Restart Streamora configuration",
  "restart",
  restart_streamora
)

%include "sources.liq"
%include "schedule.liq"

def auth_locutor(login) =
  payload = json.stringify({
    username = login.user,
    password = login.password
  })

  response = http.post(
    "http://application:8080/liquidsoap/auth",
    headers=[("Content-Type", "application/json")],
    data=payload
  )

  response.status_code == 200
end

live = input.harbor(
  environment.get("LIQUIDSOAP_HARBOR_MOUNT"),
  port=8005,
  auth=auth_locutor
)

output_source = fallback(track_sensitive=false, [live, radio])

output.icecast(
  %mp3,
  host="icecast",
  port=8000,
  password=environment.get("ICECAST_SOURCE_PASSWORD"),
  mount=environment.get("ICECAST_MOUNT"),
  name="Streamora",
  description="Streamora Auto DJ",
  output_source
)
