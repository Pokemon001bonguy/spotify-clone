package com.oscarliang.spotifyclone.feature.playlist;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.model.Playlist;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class PlaylistViewModel extends ViewModel {

    private final PlaylistRepository playlistRepository;
    private final Observable<Result<Pair<Playlist, List<Music>>>> result;

    @VisibleForTesting
    final BehaviorSubject<String> playlistId = BehaviorSubject.create();

    @Inject
    public PlaylistViewModel(
            PlaylistRepository playlistRepository,
            MusicRepository musicRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.result = playlistId.switchMap(id -> {
                    if (id == null || id.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return playlistRepository
                                .getPlaylistById(id)
                                .flatMap(
                                        playlist -> musicRepository.getMusicsByIds(playlist.getMusicIds()),
                                        (playlist, music) -> Pair.create(playlist, music)
                                )
                                .map(data -> Result.success(data))
                                .onErrorReturn(throwable -> Result.error(throwable.getMessage(), null))
                                .toObservable()
                                .startWithItem(Result.loading());
                    }
                });
        // Since the playlist may be updated from
        // elsewhere, we do not cache the current data
    }

    public Observable<Result<Pair<Playlist, List<Music>>>> getResult() {
        return result;
    }

    public void setPlaylistId(@NonNull String id) {
        if (Objects.equals(playlistId.getValue(), id)) {
            return;
        }
        playlistId.onNext(id);
    }

    public void retry() {
        String current = playlistId.getValue();
        if (current != null && !current.isEmpty()) {
            playlistId.onNext(current);
        }
    }

    public Completable deletePlaylist(String id) {
        return playlistRepository.deletePlaylist(id);
    }

}