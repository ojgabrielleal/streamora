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

output.icecast(
  %mp3,
  host="icecast",
  port=8000,
  password=environment.get("ICECAST_SOURCE_PASSWORD"),
  mount=environment.get("ICECAST_MOUNT"),
  name="Streamora",
  description="Streamora Auto DJ",
  radio
)
