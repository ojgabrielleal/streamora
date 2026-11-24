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

current_locutor = ref("")

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

  if response.status_code == 200 then
    current_locutor := login.user
    true
  else
    false
  end
end

live = input.harbor(
  environment.get("LIQUIDSOAP_HARBOR_MOUNT"),
  port=8005,
  auth=auth_locutor,
  on_disconnect=fun () -> current_locutor := ""
)

output_source = fallback(track_sensitive=false, [live, radio])

current_track = ref({title="", artist="", filename=""})

def update_current_track(m) =
  current_track := {
    title = m["title"] == "" ? path.basename(m["filename"]) : m["title"],
    artist = m["artist"],
    filename = m["filename"]
  }
end

output_source.on_metadata(update_current_track)

server.register(
  namespace="streamora",
  description="Get currently playing track metadata as JSON",
  "now_playing",
  fun (_) -> json.stringify({
    title = current_track().title,
    artist = if current_locutor() != "" then current_locutor() else current_track().artist end,
    filename = current_track().filename
  })
)

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
