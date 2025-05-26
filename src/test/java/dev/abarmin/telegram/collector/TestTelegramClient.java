package dev.abarmin.telegram.collector;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPaidMedia;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.ReplaceStickerInSet;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumbnail;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class TestTelegramClient implements TelegramClient {

    private final TestTelegramMessageCollector messageCollector;

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(Method method) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        if (method instanceof SendMessage message) {
            messageCollector.collect(message);
            return null;
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public Message execute(SendDocument sendDocument) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message execute(SendPhoto sendPhoto) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean execute(SetWebhook setWebhook) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message execute(SendVideo sendVideo) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message execute(SendVideoNote sendVideoNote) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message execute(SendSticker sendSticker) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message execute(SendAudio sendAudio) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message execute(SendVoice sendVoice) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Message> execute(SendPaidMedia sendPaidMedia) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean execute(SetChatPhoto setChatPhoto) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean execute(AddStickerToSet addStickerToSet) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean execute(ReplaceStickerInSet replaceStickerInSet) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean execute(SetStickerSetThumbnail setStickerSetThumbnail) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean execute(CreateNewStickerSet createNewStickerSet) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File execute(UploadStickerFile uploadStickerFile) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Serializable execute(EditMessageMedia editMessageMedia) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.io.File downloadFile(File file) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream downloadFileAsStream(File file) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message execute(SendAnimation sendAnimation) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendDocument sendDocument) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendPhoto sendPhoto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetWebhook setWebhook) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVideo sendVideo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVideoNote sendVideoNote) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendSticker sendSticker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendAudio sendAudio) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVoice sendVoice) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<Message>> executeAsync(SendMediaGroup sendMediaGroup) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<Message>> executeAsync(SendPaidMedia sendPaidMedia) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetChatPhoto setChatPhoto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(AddStickerToSet addStickerToSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(ReplaceStickerInSet replaceStickerInSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetStickerSetThumbnail setStickerSetThumbnail) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(CreateNewStickerSet createNewStickerSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<File> executeAsync(UploadStickerFile uploadStickerFile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Serializable> executeAsync(EditMessageMedia editMessageMedia) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendAnimation sendAnimation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<java.io.File> downloadFileAsync(File file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<InputStream> downloadFileAsStreamAsync(File file) {
        throw new UnsupportedOperationException();
    }
}
