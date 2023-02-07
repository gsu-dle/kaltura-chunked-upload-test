import com.kaltura.client.enums.*;
import com.kaltura.client.types.*;
import com.kaltura.client.utils.response.base.Response;
import com.kaltura.client.services.*;
import com.kaltura.client.services.MediaService.AddContentMediaBuilder;
import com.kaltura.client.services.MediaService.AddMediaBuilder;
import com.kaltura.client.services.MediaService.GetMediaBuilder;
import com.kaltura.client.services.MediaService.UpdateContentMediaBuilder;
import com.kaltura.client.APIOkRequestsExecutor;
import com.kaltura.client.Client;
import com.kaltura.client.Configuration;
import com.kaltura.client.ILogger;
import com.kaltura.client.Logger;
import com.kaltura.client.utils.response.OnCompletion;
import chunkedupload.ParallelUpload;
import java.util.Arrays;

public class UploadSession {
	private static ILogger logger = Logger.getLogger(UploadSession.class);

	public static void main(String[] argv) {
		try {
			logger.info("argv: " + Arrays.toString(argv));
			if (argv.length < 11) {
				throw new IllegalArgumentException();
			}

			try {
				/**
				 * Get argument values
				 */
				String serviceURL = argv[0]; // <service URL>
				int partnerId = Integer.parseInt(argv[1]); // <partner ID>
				String secret = argv[2]; // <partner admin secret>
				String filePath = argv[3]; // </path/to/file>
				String name = argv[4]; // <file name>
				String userId = argv[5]; // <user>
				String ks = argv[6]; // <sessionid>
				String uploadUserId = argv[8]; // <upload_user_id>
				if (uploadUserId == null || uploadUserId.isEmpty()) {
					uploadUserId = userId;
				}
				String folderName = argv[9]; // <folder name>
				String entryId = argv.length > 10 ? argv[10] : null;

				/**
				 * Init Kaltura API objects
				 */
				Configuration config = new Configuration();
				config.setEndpoint(serviceURL);

				Client client = new Client(config);
				if (ks == null || ks.isEmpty()) {
					ks = client.generateSessionV2(secret, uploadUserId, SessionType.ADMIN, partnerId, 86400, "");
				}
				client.setSessionId(ks);
				logger.info("ks: '" + ks + "'");

				/**
				 * Do the thing
				 */
				MediaEntry newEntry = null;
				if (entryId != null && !entryId.isEmpty()) {
					GetMediaBuilder getBuilder = MediaService.get(entryId);
					Response<?> response = APIOkRequestsExecutor.getExecutor().execute(getBuilder.build(client));
					MediaEntry results = null;
					if (response != null && response.results instanceof MediaEntry) {
						results = (MediaEntry) response.results;
					}

					if (results != null && results.getId() != null) {
						doUpload(client, filePath, entryId, true);
					} else {
						logger.error("No such entry: '" + entryId + "'");
					}
				} else {
					newEntry = new MediaEntry();
					newEntry.setName(name);
					newEntry.setType(EntryType.MEDIA_CLIP);
					newEntry.setMediaType(MediaType.VIDEO);
					newEntry.setTags("panopto, " + userId + ", " + folderName);

					AddMediaBuilder requestBuilder = MediaService.add(newEntry)
							.setCompletion(new OnCompletion<Response<MediaEntry>>() {
								public void onComplete(Response<MediaEntry> result) {
									logger.debug("inside onComplete()");

									String fentryId = result.results.getId();
									if (fentryId != null) {
										logger.info("Created a new entry: '" + fentryId + "'");
										doUpload(client, filePath, fentryId, false);
									} else {
										return;
									}
								}
							});
					APIOkRequestsExecutor.getExecutor().queue(requestBuilder.build(client));
				}
			} catch (APIException e) {
				logger.error("An error occurred", e);
			}
		} catch (IllegalArgumentException iae) {
			logger.error(
					"Usage: UploadSession <service URL> <partner ID> <partner admin secret> </path/to/file> "
							+ "<file name> <user> <sessionid> <tmpdir> <upload_user_id> <folder name> "
							+ "[optional entryId to update]");
		} catch (Exception exc) {
			logger.error("A fatal error occurred", exc);
		}
	}

	public static boolean doUpload(Client client, String filePath, String entryId, boolean update) {
		ParallelUpload pu = new ParallelUpload(client, filePath);
		String tokenId = null;
		try {
			tokenId = pu.upload();
			if (tokenId == null) {
				logger.info("UploadSession.doUpload() - tokenId is null");
				return (false);
			}

			UploadedFileTokenResource fileTokenResource = new UploadedFileTokenResource();
			fileTokenResource.setToken(tokenId);
			Response<?> response = null;
			if (update == true) {
				UpdateContentMediaBuilder requestBuilder = MediaService.updateContent(entryId, fileTokenResource);
				response = APIOkRequestsExecutor.getExecutor().execute(requestBuilder.build(client));
			} else {
				AddContentMediaBuilder requestBuilder = MediaService.addContent(entryId, fileTokenResource);
				response = APIOkRequestsExecutor.getExecutor().execute(requestBuilder.build(client));
			}
			if (response != null && response.error != null) {
				logger.error(response.error);
				return (false);
			}

			logger.info("Uploaded Video file to entry: '" + entryId + "'");

			return (true);
		} catch (Exception e) {
			logger.error("UploadSession.doUpload()", e);
			return (false);
		}
	}
}
