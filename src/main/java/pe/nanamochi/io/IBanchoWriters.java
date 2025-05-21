package pe.nanamochi.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import pe.nanamochi.objects.*;

public interface IBanchoWriters {
  void writeLoginReply(OutputStream stream, int reply) throws IOException;

  void writeMessage(OutputStream stream, Message message) throws IOException;

  void writePing(OutputStream stream) throws IOException;

  void writeIrcChangeUsername(OutputStream stream, String oldName, String newName)
      throws IOException;

  void writeUserStats(OutputStream stream, UserInfo info) throws IOException;

  void writeUserQuit(OutputStream stream, UserQuit quit) throws IOException;

  void writeSpectatorJoined(OutputStream stream, int userId) throws IOException;

  void writeSpectatorLeft(OutputStream stream, int userId) throws IOException;

  void writeSpectateFrames(OutputStream stream, ReplayFrameBundle bundle) throws IOException;

  void writeVersionUpdate(OutputStream stream) throws IOException;

  void writeSpectatorCantSpectate(OutputStream stream, int userId) throws IOException;

  void writeGetAttention(OutputStream stream) throws IOException;

  void writeAnnouncement(OutputStream stream, String message) throws IOException;

  void writeMatchUpdate(OutputStream stream, Match match) throws IOException;

  void writeMatchNew(OutputStream stream, Match match) throws IOException;

  void writeMatchDisband(OutputStream stream, int matchId) throws IOException;

  void writeLobbyJoin(OutputStream stream, int userId) throws IOException;

  void writeLobbyPart(OutputStream stream, int userId) throws IOException;

  void writeMatchJoinSuccess(OutputStream stream, Match match) throws IOException;

  void writeMatchJoinFail(OutputStream stream) throws IOException;

  void writeFellowSpectatorJoined(OutputStream stream, int userId) throws IOException;

  void writeFellowSpectatorLeft(OutputStream stream, int userId) throws IOException;

  void writeMatchStart(OutputStream stream, Match match) throws IOException;

  void writeMatchScoreUpdate(OutputStream stream, ScoreFrame frame) throws IOException;

  void writeMatchTransferHost(OutputStream stream) throws IOException;

  void writeMatchAllPlayersLoaded(OutputStream stream) throws IOException;

  void writeMatchPlayerFailed(OutputStream stream, long slotId) throws IOException;

  void writeMatchComplete(OutputStream stream) throws IOException;

  void writeMatchSkip(OutputStream stream) throws IOException;

  void writeUnauthorized(OutputStream stream) throws IOException;

  void writeChannelJoinSuccess(OutputStream stream, String channel) throws IOException;

  void writeChannelRevoked(OutputStream stream, String channel) throws IOException;

  void writeChannelAvailable(OutputStream stream, Channel channel) throws IOException;

  void writeChannelAvailableAutojoin(OutputStream stream, Channel channel) throws IOException;

  void writeBeatmapInfoReply(OutputStream stream, BeatmapInfoReply reply) throws IOException;

  void writeLoginPermissions(OutputStream stream, long permissions) throws IOException;

  void writeFriendsList(OutputStream stream, List<Integer> userIds) throws IOException;

  void writeProtocolNegotiation(OutputStream stream, int version) throws IOException;

  void writeTitleUpdate(OutputStream stream, TitleUpdate update) throws IOException;

  void writeMonitor(OutputStream stream) throws IOException;

  void writeMatchPlayerSkipped(OutputStream stream, int slotId) throws IOException;

  void writeUserPresence(OutputStream stream, UserInfo info) throws IOException;

  void writeRestart(OutputStream stream, int retryMs) throws IOException;

  void writeInvite(OutputStream stream, Message message) throws IOException;

  void writeChannelInfoComplete(OutputStream stream) throws IOException;

  void writeMatchChangePassword(OutputStream stream, String password) throws IOException;

  void writeSilenceInfo(OutputStream stream, int timeRemaining) throws IOException;

  void writeUserSilenced(OutputStream stream, long userId) throws IOException;

  void writeUserPresenceSingle(OutputStream stream, UserInfo info) throws IOException;

  void writeUserPresenceBundle(OutputStream stream, List<UserInfo> infos) throws IOException;

  void writeUserDMsBlocked(OutputStream stream, String targetName) throws IOException;

  void writeTargetIsSilenced(OutputStream stream, String targetName) throws IOException;

  void writeVersionUpdateForced(OutputStream stream) throws IOException;

  void writeSwitchServer(OutputStream stream, int target) throws IOException;

  void writeAccountRestricted(OutputStream stream) throws IOException;

  void writeRTX(OutputStream stream, String message) throws IOException;

  void writeMatchAbort(OutputStream stream) throws IOException;

  void writeSwitchTournamentServer(OutputStream stream, String ip) throws IOException;
}
